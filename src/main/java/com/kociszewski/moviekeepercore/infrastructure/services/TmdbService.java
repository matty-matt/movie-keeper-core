package com.kociszewski.moviekeepercore.infrastructure.services;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.cast.Cast;
import com.kociszewski.moviekeepercore.domain.movie.info.*;
import com.kociszewski.moviekeepercore.domain.movie.info.releases.Releases;
import com.kociszewski.moviekeepercore.domain.trailers.TrailerSection;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import com.kociszewski.moviekeepercore.infrastructure.exception.MovieNotFoundException;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieId;
import com.kociszewski.moviekeepercore.shared.model.SearchMovieResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TmdbService implements ExternalService {

    private final TmdbClient tmdbClient;

    @Override
    public ExternalMovieId searchMovie(SearchPhrase searchPhrase, MovieId movieId) {
        ExternalMovieId externalMovieId = tmdbClient.search(searchPhrase.getPhrase())
                .get()
                .retrieve()
                .bodyToMono(SearchMovieResult.class)
                .block()
                .getResults()
                .stream()
                .findFirst()
                .orElseThrow(() -> new MovieNotFoundException(String.format("Movie with title '%s' not found.", searchPhrase.getPhrase())));
        return externalMovieId;
    }

    @Override
    public ExternalMovie fetchMovieDetails(ExternalMovieId externalMovieId) {

        ExternalMovie movie = tmdbClient.movieDetails(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(ExternalMovie.class)
                .block();

        System.out.println(movie);

        // getDetails should be also called here
        return movie;
    }

    @Override
    public Releases getReleases(MovieId movieId) {
        return null;
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
