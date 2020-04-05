package com.kociszewski.movieservice.domain;

import com.kociszewski.movieservice.domain.commands.*;
import com.kociszewski.movieservice.domain.events.*;
import com.kociszewski.movieservice.domain.info.*;
import com.kociszewski.movieservice.domain.info.Runtime;
import com.kociszewski.movieservice.shared.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.List;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;
import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@Getter
@Slf4j
public class MovieAggregate {

    @AggregateIdentifier
    private String movieId;

    private String externalMovieId;
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
                command.getPhrase()));
    }

    @EventSourcingHandler
    private void on(MovieSearchDelegatedEvent event) {
        this.movieId = event.getMovieId();
        this.searchPhrase = new SearchPhrase(event.getSearchPhrase());
    }

    @CommandHandler
    public void handle(SaveMovieCommand command) {
        if (this.externalMovieId == null) {
            apply(new MovieSavedEvent(command.getMovieId(), command.getExternalMovie()));
        }
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
    public void handle(FindCastCommand command) {
        apply(new CastSearchDelegatedEvent(command.getMovieId(), command.getExternalMovieId()));
    }

    @CommandHandler
    public void handle(FindTrailersCommand command) {
        apply(new TrailersSearchDelegatedEvent(command.getMovieId(), command.getExternalMovieId()));
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
//        apply(new TrailersDeletedEvent(trailerEntity.getTrailerEntityId()));
//        apply(new CastDeletedEvent(castEntity.getCastEntityId()));
    }

    @EventSourcingHandler
    private void on(MovieDeletedEvent event) {
        markDeleted();
    }
}
