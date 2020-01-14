package com.kociszewski.moviekeepercore.movie;

import com.kociszewski.moviekeepercore.movie.info.MovieInfo;
import com.kociszewski.moviekeepercore.movie.releases.ReleaseDate;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Date;

@Aggregate
public class MovieAggregate {

    @AggregateIdentifier
    private MovieId movieId;
    private MovieInfo movieInfo;
    private ReleaseDate releaseDate;
    private Watched watched;
    private Date lastRefreshDate;
    private Date findDate;

}
