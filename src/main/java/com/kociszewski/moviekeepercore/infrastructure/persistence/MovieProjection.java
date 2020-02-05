package com.kociszewski.moviekeepercore.infrastructure.persistence;

import com.kociszewski.moviekeepercore.domain.movie.events.MovieSavedEvent;
import com.kociszewski.moviekeepercore.domain.movie.queries.FindMovieQuery;
import com.kociszewski.moviekeepercore.infrastructure.exception.MovieNotFoundException;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieInfo;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MovieProjection {

    private final MovieRepository movieRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @EventHandler
    public void handle(MovieSavedEvent event) {
        ExternalMovieInfo externalMovieInfo = event.getExternalMovie().getExternalMovieInfo();
        MovieDTO movie = MovieDTO.builder()
                .id(event.getMovieId().getId())
                .externalMovieId(event.getExternalMovie().getExternalMovieId().getId())
                .posterPath(externalMovieInfo.getPosterPath())
                .title(externalMovieInfo.getTitle())
                .originalTitle(externalMovieInfo.getOriginalTitle())
                .overview(externalMovieInfo.getOverview())
                .releaseDateDigital(event.getExternalMovie().getDigitalRelease())
                .releaseDate(externalMovieInfo.getReleaseDate())
                .originalLanguage(externalMovieInfo.getOriginalLanguage())
                .voteAverageMdb(externalMovieInfo.getVoteAverage())
                .voteCount(externalMovieInfo.getVoteCount())
                .runtime(externalMovieInfo.getRuntime())
                .genres(externalMovieInfo.getGenres())
                // TODO watched, creationDate, lastRefreshDate <- should be taken from aggregate
                .build();
        movieRepository.save(movie);
        queryUpdateEmitter.emit(FindMovieQuery.class, query -> true, movie);
    }

    @QueryHandler
    public MovieDTO handle(FindMovieQuery findMovieQuery) {
        return movieRepository.
                findById(findMovieQuery.getMovieId().getId())
                .orElseGet(MovieDTO::new);
    }
}
