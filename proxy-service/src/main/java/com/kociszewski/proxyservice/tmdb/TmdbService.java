package com.kociszewski.proxyservice.tmdb;

import com.kociszewski.proxyservice.movierelease.MovieReleaseService;
import com.kociszewski.proxyservice.movierelease.ReleasesResult;
import com.kociszewski.proxyservice.shared.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class TmdbService {

    private final TmdbClient tmdbClient;
    private final MovieReleaseService movieReleaseService;

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
        externalMovieInfo.setInsertionDate(new Date());
        externalMovieInfo.setLastRefreshDate(new Date());
        externalMovieInfo.setWatched(false);
        String digitalRelease = retrieveDigitalRelease(externalMovieId);

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

    public String retrieveDigitalRelease(ExternalMovieId externalMovieId) {
        ReleasesResult releasesResult = tmdbClient.releases(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(ReleasesResult.class)
                .block();

        return movieReleaseService.digitalRelease(releasesResult);
    }

    public VoteDTO retrieveVote(ExternalMovieId externalMovieId) {
        // TODO check vote model and if it should be done using commands/queries/sagas(as it is refresh)
        return tmdbClient.movieDetails(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(VoteDTO.class)
                .block();
    }

    public TrailerSectionDTO retrieveTrailers(ExternalMovieId externalMovieId) {
        return tmdbClient.trailers(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(TrailerSectionDTO.class)
                .block();
    }

    public CastDTO retrieveCast(ExternalMovieId externalMovieId) {
        return tmdbClient.cast(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(CastDTO.class)
                .block();
    }
}
