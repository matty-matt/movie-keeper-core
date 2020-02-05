package com.kociszewski.moviekeepercore.domain;

import com.kociszewski.moviekeepercore.domain.cast.Cast;
import com.kociszewski.moviekeepercore.domain.movie.info.*;
import com.kociszewski.moviekeepercore.domain.trailers.TrailerSection;
import com.kociszewski.moviekeepercore.infrastructure.exception.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;

public interface ExternalService {
    ExternalMovie searchMovie(SearchPhrase searchPhrase, MovieId movieId) throws NotFoundInExternalServiceException;
    String getDigitalRelease(ExternalMovieId externalMovieId);
    Vote getVote(MovieId movieId);
    TrailerSection getTrailers(MovieId movieId);
    Cast getCast(MovieId movieId);
}
