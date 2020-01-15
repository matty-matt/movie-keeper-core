package com.kociszewski.moviekeepercore.infrastructure.services;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.domain.cast.Cast;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieInfo;
import com.kociszewski.moviekeepercore.domain.movie.info.Title;
import com.kociszewski.moviekeepercore.domain.movie.info.Vote;
import com.kociszewski.moviekeepercore.domain.movie.info.releases.Releases;
import com.kociszewski.moviekeepercore.domain.trailers.TrailerSection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TmdbService implements ExternalService {

    private static final String SEARCH_MOVIE_ENDPOINT = "/search/movie";
    private static final String API_KEY = "api_key";
    private static final String QUERY = "query";
    private static final String LANGUAGE = "language";
    private static final String PL_PL = "pl-PL";
    private final String apiKey;
    private final String baseUrl;

    public TmdbService(@Value("${api.url}") String baseUrl, @Value("${api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    @Override
    public MovieInfo findMovie(Title title) {

        WebClient webClient = WebClient
                .builder()
                .baseUrl(buildSearchUri(SEARCH_MOVIE_ENDPOINT, title.getTitle()))
                .build();

        String response = webClient
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println(response);
        // getDetails should be also called here
        return null;
    }

    private UriComponentsBuilder buildBaseUri(String endpoint) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl.concat(endpoint))
                .queryParam(API_KEY, apiKey);
    }

    private String buildSearchUri(String endpoint, String query) {
        return buildBaseUri(endpoint)
                .queryParam(QUERY, query)
                .queryParam(LANGUAGE, PL_PL)
                .build(false).toUriString();
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
