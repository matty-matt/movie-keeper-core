package com.kociszewski.moviekeeper.tmdb;

import com.kociszewski.moviekeeper.shared.*;
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

    public ExternalMovie searchMovie(String searchPhrase) throws NotFoundInExternalServiceException {
        String externalMovieId = tmdbClient.search(searchPhrase)
                .get()
                .retrieve()
                .bodyToMono(SearchMovieResult.class)
                .block()
                .getResults()
                .stream()
                .findFirst()
                .orElseThrow(NotFoundInExternalServiceException::new).getId();

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

    private ExternalMovieInfo fetchMovieDetails(String externalMovieId) {
        return tmdbClient.movieDetails(externalMovieId)
                .get()
                .retrieve()
                .bodyToMono(ExternalMovieInfo.class)
                .block();
    }

    public String retrieveDigitalRelease(String externalMovieId) {
        ReleasesResult releasesResult = tmdbClient.releases(externalMovieId)
                .get()
                .retrieve()
                .bodyToMono(ReleasesResult.class)
                .block();

        return movieReleaseService.digitalRelease(releasesResult);
    }

    public VoteDTO retrieveVote(String externalMovieId) {
        // TODO check vote model and if it should be done using commands/queries/sagas(as it is refresh)
        return tmdbClient.movieDetails(externalMovieId)
                .get()
                .retrieve()
                .bodyToMono(VoteDTO.class)
                .block();
    }

    public TrailerSectionDTO retrieveTrailers(String externalMovieId) {
        return tmdbClient.trailers(externalMovieId)
                .get()
                .retrieve()
                .bodyToMono(TrailerSectionDTO.class)
                .block();
    }

    public CastDTO retrieveCast(String externalMovieId) {
        return tmdbClient.cast(externalMovieId)
                .get()
                .retrieve()
                .bodyToMono(CastDTO.class)
                .block();
    }
}
