package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.*;
import com.kociszewski.moviekeeper.domain.events.*;
import com.kociszewski.moviekeeper.domain.info.*;
import com.kociszewski.moviekeeper.domain.info.Runtime;
import com.kociszewski.moviekeeper.infrastructure.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;
import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@NoArgsConstructor
@Slf4j
@Getter
public class MovieAggregate {

    @AggregateIdentifier
    private String movieId;

    private String trailersId;
    private String castId;

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
    public void handle(DelegateTrailersAndCastSearchCommand command) {
        if (castId == null || trailersId == null) {
            String trailersId = UUID.randomUUID().toString();
            String castId = UUID.randomUUID().toString();
            apply(new TrailersAndCastSearchDelegatedEvent(command.getMovieId(), externalMovieId, trailersId, castId));
        }
    }

    @EventSourcingHandler
    private void on(TrailersAndCastSearchDelegatedEvent event) {
        this.trailersId = event.getTrailersId();
        this.castId = event.getCastId();
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
        apply(new MovieDeletedEvent(command.getMovieId(), trailersId, castId));
    }

    @EventSourcingHandler
    private void on(MovieDeletedEvent event) {
        markDeleted();
    }
}
