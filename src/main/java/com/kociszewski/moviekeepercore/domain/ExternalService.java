package com.kociszewski.moviekeepercore.domain;

import com.kociszewski.moviekeepercore.domain.cast.Cast;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieInfo;
import com.kociszewski.moviekeepercore.domain.movie.info.Title;
import com.kociszewski.moviekeepercore.domain.movie.info.Vote;
import com.kociszewski.moviekeepercore.domain.movie.info.releases.Releases;
import com.kociszewski.moviekeepercore.domain.trailers.TrailerSection;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;

public interface ExternalService {
    ExternalMovieId searchMovie(Title title);
    MovieInfo findMovie(ExternalMovieId externalMovieId);
    Releases getReleases(MovieId movieId);
    Vote getVote(MovieId movieId);
    TrailerSection getTrailers(MovieId movieId);
    Cast getCast(MovieId movieId);
}
