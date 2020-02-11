package com.kociszewski.moviekeepercore.domain;

import com.kociszewski.moviekeepercore.infrastructure.movie.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
import com.kociszewski.moviekeepercore.infrastructure.vote.VoteDTO;
import com.kociszewski.moviekeepercore.shared.model.*;

public interface ExternalService {
    ExternalMovie searchMovie(SearchPhrase searchPhrase) throws NotFoundInExternalServiceException;
    String getDigitalRelease(ExternalMovieId externalMovieId);
    VoteDTO getVote(ExternalMovieId externalMovieId);
    TrailerSectionDTO getTrailers(ExternalMovieId externalMovieId);
    Cast getCast(ExternalMovieId externalMovieId);
}
