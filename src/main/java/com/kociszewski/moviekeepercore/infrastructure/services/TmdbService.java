package com.kociszewski.moviekeepercore.infrastructure.services;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.cast.Cast;
import com.kociszewski.moviekeepercore.domain.movie.info.*;
import com.kociszewski.moviekeepercore.domain.trailers.TrailerSection;
import com.kociszewski.moviekeepercore.infrastructure.exception.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.infrastructure.model.movierelease.ReleasesResult;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieInfo;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import com.kociszewski.moviekeepercore.shared.model.SearchMovieResult;
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
    public ExternalMovie searchMovie(SearchPhrase searchPhrase, MovieId movieId) throws NotFoundInExternalServiceException {
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
    public Vote getVote(MovieId movieId) {
        return null;
    }

    @Override
    public TrailerSection getTrailers(MovieId movieId) {
        return null;
    }

    @Override
    public Cast getCast(MovieId movieId) {
        return null;
    }
}
