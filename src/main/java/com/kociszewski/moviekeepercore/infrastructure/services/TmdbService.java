package com.kociszewski.moviekeepercore.infrastructure.services;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.infrastructure.exception.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.infrastructure.model.movierelease.ReleasesResult;
import com.kociszewski.moviekeepercore.shared.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TmdbService implements ExternalService {

    private final TmdbClient tmdbClient;
    private final MovieReleaseService movieReleaseService;

    @Override
    public ExternalMovie searchMovie(SearchPhrase searchPhrase) throws NotFoundInExternalServiceException {
        ExternalMovieId externalMovieId = tmdbClient.search(searchPhrase.getPhrase())
                .get()
                .retrieve()
                .bodyToMono(SearchMovieResult.class)
                .block()
                .getResults()
                .stream()
                .findFirst()
                .orElseThrow(NotFoundInExternalServiceException::new);

        ExternalMovieInfo externalMovieInfo = fetchMovieDetails(externalMovieId);
        String digitalRelease = getDigitalRelease(externalMovieId);
        return ExternalMovie.builder()
                .externalMovieId(externalMovieId)
                .externalMovieInfo(externalMovieInfo)
                .digitalRelease(digitalRelease)
                .build();
    }

    private ExternalMovieInfo fetchMovieDetails(ExternalMovieId externalMovieId) {
        return tmdbClient.movieDetails(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(ExternalMovieInfo.class)
                .block();
    }

    @Override
    public String getDigitalRelease(ExternalMovieId externalMovieId) {
        ReleasesResult releasesResult = tmdbClient.releases(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(ReleasesResult.class)
                .block();

        return movieReleaseService.getDigitalRelease(releasesResult);
    }

    @Override
    public ExternalVote getVote(ExternalMovieId externalMovieId) {
        // TODO check vote model and if it should be done using commands/queries/sagas(as it is refresh)
        return tmdbClient.movieDetails(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(ExternalVote.class)
                .block();
    }

    @Override
    public ExternalTrailerSection getTrailers(ExternalMovieId externalMovieId) {
        // TODO check trailers model and if it should be done using commands/queries to entity
        return tmdbClient.trailers(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(ExternalTrailerSection.class)
                .block();
    }

    @Override
    public ExternalCast getCast(ExternalMovieId externalMovieId) {
        // TODO check cast model and if it should be done using commands/queries to entity
        return tmdbClient.cast(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(ExternalCast.class)
                .block();
    }
}
