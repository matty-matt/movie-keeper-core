package com.kociszewski.moviekeepercore.infrastructure.services;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.cast.Cast;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieInfo;
import com.kociszewski.moviekeepercore.domain.movie.info.Title;
import com.kociszewski.moviekeepercore.domain.movie.info.Vote;
import com.kociszewski.moviekeepercore.domain.movie.info.releases.Releases;
import com.kociszewski.moviekeepercore.domain.trailers.TrailerSection;
import com.kociszewski.moviekeepercore.infrastructure.access.model.ExternalMovie;
import com.kociszewski.moviekeepercore.infrastructure.exception.MovieNotFoundException;
import com.kociszewski.moviekeepercore.infrastructure.access.model.FoundMovieId;
import com.kociszewski.moviekeepercore.infrastructure.access.model.SearchMovieResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TmdbService implements ExternalService {

    private final TmdbClient tmdbClient;

    @Override
    public MovieInfo findMovie(Title title) {

        FoundMovieId foundMovieId = tmdbClient.search(title.getTitle())
                .get()
                .retrieve()
                .bodyToMono(SearchMovieResult.class)
                .block()
                .getResults()
                .stream()
                .findFirst()
                .orElseThrow(() -> new MovieNotFoundException(String.format("Movie with title '%s' not found.", title)));

        System.out.println(foundMovieId);

        ExternalMovie movie = tmdbClient.movieDetails(foundMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(ExternalMovie.class)
                .block();

        System.out.println(movie);

        // getDetails should be also called here
        return null;
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
