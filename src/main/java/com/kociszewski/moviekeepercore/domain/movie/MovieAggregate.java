package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.movie.commands.FindMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.commands.SaveMovieCommand;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSavedEvent;
import com.kociszewski.moviekeepercore.domain.movie.events.MovieSearchDelegatedEvent;
import com.kociszewski.moviekeepercore.domain.movie.info.*;
import com.kociszewski.moviekeepercore.domain.movie.info.Runtime;
import com.kociszewski.moviekeepercore.domain.trailers.TrailerSection;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import lombok.Data;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@Data
public class MovieAggregate {

    @AggregateIdentifier
    private MovieId movieId;
    private ExternalMovieId externalMovieId;
    private List<TrailerSection> trailers;
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
        System.out.println(">>>>>> Handling MovieSearchDelegatedEvent: " + this);
        this.movieId = event.getMovieId();
        this.searchPhrase = event.getSearchPhrase();
    }

    @CommandHandler
    public void handle(SaveMovieCommand command) {
        apply(new MovieSavedEvent(command.getMovieId(), command.getExternalMovie()));
    }

    @EventSourcingHandler
    private void on(MovieSavedEvent event) {
        System.out.println(">>>>>>> Handling MovieSavedEvent:: " + this);
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
        // TODO trailers
    }

}
