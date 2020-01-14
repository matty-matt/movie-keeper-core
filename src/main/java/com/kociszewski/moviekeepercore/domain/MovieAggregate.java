package com.kociszewski.moviekeepercore.domain;

import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieInfo;
import com.kociszewski.moviekeepercore.domain.movie.info.Watched;
import com.kociszewski.moviekeepercore.domain.movie.releases.ReleaseDate;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Date;

@Aggregate
public class MovieAggregate {

    @AggregateIdentifier
    private MovieId movieId;
    private TmdbId tmdbId;
    private MovieInfo movieInfo;
    private ReleaseDate releaseDate;
    private Watched watched;
    private Date lastRefreshDate;
    private Date findDate;

}
