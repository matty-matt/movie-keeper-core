package com.kociszewski.moviekeepercore.domain;

import com.kociszewski.moviekeepercore.infrastructure.movie.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.shared.model.*;

public interface ExternalService {
    ExternalMovie searchMovie(SearchPhrase searchPhrase) throws NotFoundInExternalServiceException;
    String getDigitalRelease(ExternalMovieId externalMovieId);
    ExternalVote getVote(ExternalMovieId externalMovieId);
    ExternalTrailerSection getTrailers(ExternalMovieId externalMovieId);
    ExternalCast getCast(ExternalMovieId externalMovieId);
}
