package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.movie.commands.DeleteMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.commands.FindMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.commands.SaveMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.commands.ToggleWatchedCommand;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieDeletedEvent;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSavedEvent;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSearchDelegatedEvent;
import com.kociszewski.moviekeepercore.domain.movie.events.ToggleWatchedEvent;
import com.kociszewski.moviekeepercore.domain.movie.info.Runtime;
import com.kociszewski.moviekeepercore.domain.movie.info.*;
import com.kociszewski.moviekeepercore.domain.trailer.commands.DeleteTrailersCommand;
import com.kociszewski.moviekeepercore.domain.trailer.commands.SaveTrailersCommand;
import com.kociszewski.moviekeepercore.domain.trailer.events.TrailersDeletedEvent;
import com.kociszewski.moviekeepercore.domain.trailer.events.TrailersSavedEvent;
import com.kociszewski.moviekeepercore.shared.model.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;
import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@Data
@Slf4j
public class MovieAggregate {

    @AggregateIdentifier
    private MovieId movieId;
    private ExternalMovieId externalMovieId;
    private Cast cast;
    private List<Trailer> trailers;
    private Poster poster;
    private Title title;
    private Title originalTitle;
    private Overview overview;
    private Language originalLanguage;
    private Vote vote;
    private Runtime runtime;
    private List<Genre> genres;
    private Release digitalRelease;
    private Release premiereRelease;
    private Watched watched;
    private Date insertionDate;
    private Date lastRefreshDate;
    private SearchPhrase searchPhrase;

    private MovieAggregate() {}

    @CommandHandler
    public MovieAggregate(FindMovieCommand command) {
        apply(new MovieSearchDelegatedEvent(command.getMovieId(), command.getPhrase()));
    }

    @EventSourcingHandler
    private void on(MovieSearchDelegatedEvent event) {
        this.movieId = event.getMovieId();
        this.searchPhrase = event.getSearchPhrase();
    }

    @CommandHandler
    public void handle(SaveMovieCommand command) {
        if (this.externalMovieId != null) {
            log.info("Movie already saved, skipping..");
        }
        apply(new MovieSavedEvent(command.getMovieId(), command.getExternalMovie()));
    }

    @EventSourcingHandler
    private void on(MovieSavedEvent event) {
        this.externalMovieId = event.getExternalMovie().getExternalMovieId();
        var movieInfo = event.getExternalMovie().getExternalMovieInfo();
        this.poster = new Poster(movieInfo.getPosterPath());
        this.title = new Title(movieInfo.getTitle());
        this.originalTitle = new Title(movieInfo.getOriginalTitle());
        this.overview = new Overview(movieInfo.getOverview());
        this.digitalRelease = new Release(event.getExternalMovie().getDigitalRelease());
        this.premiereRelease = new Release(movieInfo.getReleaseDate());
        this.vote = new Vote(movieInfo.getVoteAverage(), movieInfo.getVoteCount());
        this.runtime = new Runtime(movieInfo.getRuntime());
        this.insertionDate = new Date();
        this.lastRefreshDate = new Date();
        this.watched = new Watched(false);
        this.genres = movieInfo.getGenres().stream()
                .map(genre -> new Genre(genre.getId(), genre.getName()))
                .collect(Collectors.toList());
        // TODO cast
    }

    @CommandHandler
    public void handle(ToggleWatchedCommand command) {
        if (this.watched.isWatched() == command.getWatched().isWatched()) {
            log.info("Cannot toggle to the same state, skipping..");
        }
        apply(new ToggleWatchedEvent(command.getMovieId(), command.getWatched()));
    }

    @EventSourcingHandler
    private void on(ToggleWatchedEvent event) {
        this.watched = event.getWatched();
    }

    @CommandHandler
    public void handle(DeleteMovieCommand command) {
        apply(new MovieDeletedEvent(command.getMovieId()));
    }

    @EventSourcingHandler
    public void on(MovieDeletedEvent event) {
        markDeleted();
    }

    ////////////////////////////////////TRAILERS SECTION TO MOVE

    @CommandHandler
    public void handle(SaveTrailersCommand command) {
        if (trailers == null || trailers.isEmpty()) {
            apply(new TrailersSavedEvent(command.getMovieId(), command.getTrailerSectionDTO()));
        }
    }

    @EventSourcingHandler
    public void on(TrailersSavedEvent event) {
        this.trailers = event.getTrailerSectionDTO().getTrailers()
                .stream()
                .map(dto -> Trailer.builder()
                        .language(dto.getLanguage())
                        .country(dto.getCountry())
                        .key(dto.getKey())
                        .name(dto.getName())
                        .site(dto.getSite())
                        .size(dto.getSize()).build()).collect(Collectors.toList());
    }

    @CommandHandler
    public void handle(DeleteTrailersCommand command) {
        apply(new TrailersDeletedEvent(command.getMovieId()));
    }

    @EventSourcingHandler
    public void on(TrailersDeletedEvent event) {
        System.out.println("Deleting trailers");
    }
}
