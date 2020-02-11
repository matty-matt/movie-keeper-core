package com.kociszewski.moviekeepercore.infrastructure.movie;

import com.kociszewski.moviekeepercore.domain.movie.events.MovieSavedEvent;
import com.kociszewski.moviekeepercore.domain.movie.queries.FindMovieQuery;
import com.kociszewski.moviekeepercore.domain.movie.queries.GetAllMoviesQuery;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieInfo;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MovieProjection {

    private final MovieRepository movieRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @EventHandler
    public void handle(MovieSavedEvent event) {
        String externalMovieId = event.getExternalMovie().getExternalMovieId().getId();
        movieRepository.findById(externalMovieId).ifPresentOrElse(
                movie -> handleMovieDuplicate(),
                () -> persistMovie(event));
    }

    @QueryHandler
    public MovieDTO handle(FindMovieQuery findMovieQuery) {
        return movieRepository.
                findById(findMovieQuery.getMovieId().getId())
                .orElseGet(() -> new MovieDTO(MovieState.NOT_FOUND));
    }

    @QueryHandler
    public List<MovieDTO> handle(GetAllMoviesQuery getAllMoviesQuery) {
        return movieRepository.findAll();
    }

    private void handleMovieDuplicate() {
        notifySubscribers(new MovieDTO(MovieState.ALREADY_ADDED));
    }

    private void persistMovie(MovieSavedEvent event) {
        ExternalMovieInfo externalMovieInfo = event.getExternalMovie().getExternalMovieInfo();
        MovieDTO movieDTO = MovieDTO.builder()
                .id(event.getExternalMovie().getExternalMovieId().getId())
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
        movieRepository.insert(movieDTO);
        notifySubscribers(movieDTO);
    }

    private void notifySubscribers(MovieDTO movieDTO) {
        queryUpdateEmitter.emit(FindMovieQuery.class, query -> true, movieDTO);
    }
}
