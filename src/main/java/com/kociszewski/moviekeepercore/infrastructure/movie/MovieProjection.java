package com.kociszewski.moviekeepercore.infrastructure.movie;

import com.kociszewski.moviekeepercore.domain.movie.events.MovieSavedEvent;
import com.kociszewski.moviekeepercore.domain.movie.events.ToggleWatchedEvent;
import com.kociszewski.moviekeepercore.domain.movie.queries.FindMovieQuery;
import com.kociszewski.moviekeepercore.domain.movie.queries.GetAllMoviesQuery;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovieInfo;
import lombok.RequiredArgsConstructor;
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
public class MovieProjection {

    private static final String ID = "_id";
    private static final String WATCHED = "watched";
    private final MovieRepository movieRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;
    private final MongoTemplate mongoTemplate;

    @EventHandler
    public void handle(MovieSavedEvent event) {
        movieRepository.findById(event.getMovieId().getId()).ifPresentOrElse(
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

    @EventHandler
    public void handle(ToggleWatchedEvent event) {
        MovieDTO updatedMovie = mongoTemplate.findAndModify(
                Query.query(Criteria.where(ID).is(event.getMovieId().getId())),
                Update.update(WATCHED, event.getWatched().isWatched()),
                MovieDTO.class);
        notifySubscribers(updatedMovie);
    }

    private void handleMovieDuplicate() {
        notifySubscribers(new MovieDTO(MovieState.ALREADY_ADDED));
    }

    private void persistMovie(MovieSavedEvent event) {
        ExternalMovieInfo externalMovieInfo = event.getExternalMovie().getExternalMovieInfo();
        MovieDTO movieDTO = MovieDTO.builder()
                .aggregateId(event.getMovieId().getId())
                .movieId(event.getExternalMovie().getExternalMovieId().getId())
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
                // TODO watched, creationDate, lastRefreshDate <- should be taken from aggregate
                .build();
        movieRepository.insert(movieDTO);
        notifySubscribers(movieDTO);
    }

    private void notifySubscribers(MovieDTO movieDTO) {
        queryUpdateEmitter.emit(FindMovieQuery.class, query -> true, movieDTO);
    }
}
