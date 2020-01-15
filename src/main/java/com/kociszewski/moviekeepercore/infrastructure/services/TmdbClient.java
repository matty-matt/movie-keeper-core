package com.kociszewski.moviekeepercore.infrastructure.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class TmdbClient {
    private static final String SEARCH_MOVIE_ENDPOINT = "/search/movie";
    private static final String MOVIE_DETAILS_ENDPOINT = "/movie/%s";
    private static final String MOVIE_RELEASES_ENDPOINT = "/movie/%s/release_dates";
    private static final String MOVIE_TRAILERS_ENDPOINT = "/movie/%s/videos";
    private static final String MOVIE_CAST_ENDPOINT = "/movie/%s/credits";
    private static final String API_KEY = "api_key";
    private static final String QUERY = "query";
    private static final String LANGUAGE = "language";
    private static final String PL_PL = "pl-PL";
    private final String apiKey;
    private final String baseUrl;

    public TmdbClient(@Value("${api.url}") String baseUrl, @Value("${api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public WebClient searchClient(String title) {
        return WebClient
                .builder()
                .baseUrl(buildSearchUri(title))
                .build();
    }

    private String buildSearchUri(String query) {
        return buildBaseUri(SEARCH_MOVIE_ENDPOINT)
                .queryParam(QUERY, query)
                .queryParam(LANGUAGE, PL_PL)
                .build(false).toUriString();
    }

    private UriComponentsBuilder buildBaseUri(String endpoint) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl.concat(endpoint))
                .queryParam(API_KEY, apiKey);
    }
}
