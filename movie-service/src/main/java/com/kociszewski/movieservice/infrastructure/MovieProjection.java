package com.kociszewski.movieservice.infrastructure;

import com.kociszewski.movieservice.domain.events.MovieDeletedEvent;
import com.kociszewski.movieservice.domain.events.MovieSavedEvent;
import com.kociszewski.movieservice.domain.events.ToggleWatchedEvent;
import com.kociszewski.movieservice.domain.queries.GetMovieQuery;
import com.kociszewski.movieservice.domain.queries.GetAllMoviesQuery;
import com.kociszewski.movieservice.shared.ExternalMovieInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class MovieProjection {

    private static final String ID = "_id";
    private static final String WATCHED = "watched";
    private final MovieRepository movieRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;
    private final MongoTemplate mongoTemplate;

    @EventHandler
    public void handle(MovieSavedEvent event) {
        log.info("Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId());
        movieRepository.findByExternalMovieId(event.getExternalMovie().getExternalMovieId().getId()).ifPresentOrElse(
                movie -> handleMovieDuplicate(),
                () -> persistMovie(event));
    }

    @QueryHandler
    public MovieDTO handle(GetMovieQuery getMovieQuery) {
        return movieRepository.
                findById(getMovieQuery.getMovieId())
                .orElseGet(() -> new MovieDTO(MovieState.NOT_FOUND));
    }

    @QueryHandler
    public List<MovieDTO> handle(GetAllMoviesQuery getAllMoviesQuery) {
        return movieRepository.findAll();
    }

    @EventHandler
    public void handle(ToggleWatchedEvent event) {
        log.info("Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId());
        MovieDTO updatedMovie = mongoTemplate.findAndModify(
                Query.query(Criteria.where(ID).is(event.getMovieId())),
                Update.update(WATCHED, event.getWatched().isWatched()),
                MovieDTO.class);
        notifySubscribers(updatedMovie);
    }

    @EventHandler
    public void handle(MovieDeletedEvent event) {
        movieRepository.deleteById(event.getMovieId());
    }

    private void handleMovieDuplicate() {
        notifySubscribers(new MovieDTO(MovieState.ALREADY_ADDED));
    }

    private void persistMovie(MovieSavedEvent event) {
        ExternalMovieInfo externalMovieInfo = event.getExternalMovie().getExternalMovieInfo();
        MovieDTO movieDTO = MovieDTO.builder()
                .aggregateId(event.getMovieId())
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
                .genres(externalMovieInfo.getGenres()
                        .stream()
                        .map(genre -> GenreDTO.builder().id(genre.getId()).name(genre.getName()).build())
                        .collect(Collectors.toList()))
                .creationDate(externalMovieInfo.getInsertionDate())
                .lastRefreshDate(externalMovieInfo.getLastRefreshDate())
                .watched(externalMovieInfo.isWatched())
                .build();
        movieRepository.insert(movieDTO);
        notifySubscribers(movieDTO);
    }

    private void notifySubscribers(MovieDTO movieDTO) {
        queryUpdateEmitter.emit(GetMovieQuery.class, query -> true, movieDTO);
    }
}
