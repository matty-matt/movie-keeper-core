package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.cast.CastEntity;
import com.kociszewski.moviekeepercore.domain.cast.events.CastDeletedEvent;
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
import com.kociszewski.moviekeepercore.domain.trailer.TrailerEntity;
import com.kociszewski.moviekeepercore.domain.trailer.events.TrailersDeletedEvent;
import com.kociszewski.moviekeepercore.shared.model.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Collections;
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
    @AggregateMember
    private TrailerEntity trailerEntity;
    @AggregateMember
    private CastEntity castEntity;

    private ExternalMovieId externalMovieId;
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
    private DateWrapper insertionDate;
    private DateWrapper lastRefreshDate;
    private SearchPhrase searchPhrase;

    private MovieAggregate() {}

    @CommandHandler
    public MovieAggregate(FindMovieCommand command) {
        apply(new MovieSearchDelegatedEvent(
                command.getMovieId(),
                command.getTrailerEntityId(),
                command.getCastEntityId(),
                command.getPhrase()));
    }

    @EventSourcingHandler
    private void on(MovieSearchDelegatedEvent event) {
        this.movieId = event.getMovieId();
        this.trailerEntity = TrailerEntity.builder()
                .trailerEntityId(event.getTrailerEntityId())
                .trailers(Collections.emptyList())
                .build();
        this.castEntity = CastEntity.builder()
                .castEntityId(event.getCastEntityId())
                .cast(Collections.emptyList())
                .build();
        this.searchPhrase = event.getSearchPhrase();
    }

    @CommandHandler
    public void handle(SaveMovieCommand command) {
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
        this.insertionDate = new DateWrapper(movieInfo.getInsertionDate());
        this.lastRefreshDate = new DateWrapper(movieInfo.getLastRefreshDate());
        this.watched = new Watched(false);
        this.originalLanguage = new Language(movieInfo.getOriginalLanguage());
        this.genres = movieInfo.getGenres().stream()
                .map(genre -> new Genre(genre.getId(), genre.getName()))
                .collect(Collectors.toList());
    }

    @CommandHandler
    public void handle(ToggleWatchedCommand command) {
        if (this.watched.isWatched() == command.getWatched().isWatched()) {
            log.info("Cannot toggle to the same state, skipping..");
            return;
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
        apply(new TrailersDeletedEvent(trailerEntity.getTrailerEntityId()));
        apply(new CastDeletedEvent(castEntity.getCastEntityId()));
    }

    @EventSourcingHandler
    public void on(MovieDeletedEvent event) {
        markDeleted();
    }
}
