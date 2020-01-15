package com.kociszewski.moviekeepercore.infrastructure.services;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.cast.Cast;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieInfo;
import com.kociszewski.moviekeepercore.domain.movie.info.Title;
import com.kociszewski.moviekeepercore.domain.movie.info.Vote;
import com.kociszewski.moviekeepercore.domain.movie.info.releases.Releases;
import com.kociszewski.moviekeepercore.domain.trailers.TrailerSection;
import com.kociszewski.moviekeepercore.infrastructure.access.exception.MovieNotFoundException;
import com.kociszewski.moviekeepercore.infrastructure.access.model.FoundMovieId;
import com.kociszewski.moviekeepercore.infrastructure.access.model.SearchMovieResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class TmdbService implements ExternalService {

    private final TmdbClient tmdbClient;

    @Override
    public MovieInfo findMovie(Title title) {

        WebClient webClient = tmdbClient.searchClient(title.getTitle());

        FoundMovieId foundMovieId = webClient
                .get()
                .retrieve()
                .bodyToMono(SearchMovieResult.class)
                .block()
                .getResults()
                .stream()
                .findFirst()
                .orElseThrow(() -> new MovieNotFoundException(String.format("Movie with title '%s' not found.", title)));

        System.out.println(foundMovieId);
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
