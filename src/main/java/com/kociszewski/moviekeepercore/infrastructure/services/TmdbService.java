package com.kociszewski.moviekeepercore.infrastructure.services;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.cast.Cast;
import com.kociszewski.moviekeepercore.domain.movie.info.*;
import com.kociszewski.moviekeepercore.domain.movie.info.releases.Releases;
import com.kociszewski.moviekeepercore.domain.trailers.TrailerSection;
import com.kociszewski.moviekeepercore.infrastructure.model.movierelease.ReleasesResult;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import com.kociszewski.moviekeepercore.infrastructure.exception.MovieNotFoundException;
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
    public void searchMovie(SearchPhrase searchPhrase, MovieId movieId) {
        ExternalMovieId externalMovieId = tmdbClient.search(searchPhrase.getPhrase())
                .get()
                .retrieve()
                .bodyToMono(SearchMovieResult.class)
                .block()
                .getResults()
                .stream()
                .findFirst()
                .orElseThrow(() -> new MovieNotFoundException(String.format("Movie with title '%s' not found.", searchPhrase.getPhrase())));

        ExternalMovie externalMovie = fetchMovieDetails(externalMovieId);
        String digitalRelease = getDigitalRelease(externalMovieId);
        log.info("movieId={}, digitalRelease={}, payload={}", externalMovieId, digitalRelease, externalMovie);
    }

    @Override
    public ExternalMovie fetchMovieDetails(ExternalMovieId externalMovieId) {
        return tmdbClient.movieDetails(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(ExternalMovie.class)
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
