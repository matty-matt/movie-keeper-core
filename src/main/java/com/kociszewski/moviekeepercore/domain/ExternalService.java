package com.kociszewski.moviekeepercore.domain;

import com.kociszewski.moviekeepercore.domain.cast.Cast;
import com.kociszewski.moviekeepercore.domain.movie.info.*;
import com.kociszewski.moviekeepercore.domain.movie.info.releases.Releases;
import com.kociszewski.moviekeepercore.domain.trailers.TrailerSection;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;

public interface ExternalService {
    ExternalMovieId searchMovie(SearchPhrase searchPhrase);
    MovieInfo findMovie(ExternalMovieId externalMovieId);
    Releases getReleases(MovieId movieId);
    Vote getVote(MovieId movieId);
    TrailerSection getTrailers(MovieId movieId);
    Cast getCast(MovieId movieId);
}
