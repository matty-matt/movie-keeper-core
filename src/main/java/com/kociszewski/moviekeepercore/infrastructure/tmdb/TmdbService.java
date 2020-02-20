package com.kociszewski.moviekeepercore.infrastructure.tmdb;

import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastDTO;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastService;
import com.kociszewski.moviekeepercore.infrastructure.movie.NotFoundInExternalServiceException;
import com.kociszewski.moviekeepercore.infrastructure.movierelease.ReleasesResult;
import com.kociszewski.moviekeepercore.infrastructure.movierelease.MovieReleaseService;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerService;
import com.kociszewski.moviekeepercore.infrastructure.vote.VoteDTO;
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
    private final CastService castService;
    private final TrailerService trailerService;

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
        String digitalRelease = retrieveDigitalRelease(externalMovieId);

        castService.storeCast(retrieveCast(externalMovieId));
        trailerService.storeTrailers(externalMovieId.getId(), retrieveTrailers(externalMovieId));

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
    public String retrieveDigitalRelease(ExternalMovieId externalMovieId) {
        ReleasesResult releasesResult = tmdbClient.releases(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(ReleasesResult.class)
                .block();

        return movieReleaseService.digitalRelease(releasesResult);
    }

    @Override
    public VoteDTO retrieveVote(ExternalMovieId externalMovieId) {
        // TODO check vote model and if it should be done using commands/queries/sagas(as it is refresh)
        return tmdbClient.movieDetails(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(VoteDTO.class)
                .block();
    }

    @Override
    public TrailerSectionDTO retrieveTrailers(ExternalMovieId externalMovieId) {
        return tmdbClient.trailers(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(TrailerSectionDTO.class)
                .block();
    }

    private CastDTO retrieveCast(ExternalMovieId externalMovieId) {
        return tmdbClient.cast(externalMovieId.getId())
                .get()
                .retrieve()
                .bodyToMono(CastDTO.class)
                .block();
    }
}
