package com.kociszewski.moviekeepercore.domain;

import com.kociszewski.moviekeepercore.infrastructure.cast.CastDTO;
import com.kociszewski.moviekeepercore.infrastructure.movie.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
import com.kociszewski.moviekeepercore.infrastructure.vote.VoteDTO;
import com.kociszewski.moviekeepercore.shared.model.*;

public interface ExternalService {
    ExternalMovie searchMovie(SearchPhrase searchPhrase) throws NotFoundInExternalServiceException;
    String retrieveDigitalRelease(ExternalMovieId externalMovieId);
    VoteDTO retrieveVote(ExternalMovieId externalMovieId);
    TrailerSectionDTO retrieveTrailers(ExternalMovieId externalMovieId);
    CastDTO retrieveCast(ExternalMovieId externalMovieId);
}
