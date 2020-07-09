package com.kociszewski.moviekeeper.infrastructure;

import com.kociszewski.moviekeeper.domain.events.MovieDeletedEvent;
import com.kociszewski.moviekeeper.domain.events.MovieSavedEvent;
import com.kociszewski.moviekeeper.domain.events.ToggleWatchedEvent;
import com.kociszewski.moviekeeper.domain.queries.GetAllMoviesQuery;
import com.kociszewski.moviekeeper.domain.queries.GetMovieQuery;
import com.kociszewski.moviekeeper.domain.queries.GetNotSeenMoviesQuery;
import com.mongodb.bulk.BulkWriteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
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
    private static final String EXTERNAL_MOVIE_ID = "externalMovieId";
    private static final String VOTE_AVERAGE_MDB = "voteAverageMdb";
    private static final String VOTE_COUNT = "voteCount";
    private static final String RELEASE_DATE_DIGITAL = "releaseDateDigital";
    private final MovieRepository movieRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;
    private final MongoTemplate mongoTemplate;

    @EventHandler
    public void handle(MovieSavedEvent event) {
        log.info("Handling {}, id={}", event.getClass().getSimpleName(), event.getMovieId());
        movieRepository.findByExternalMovieId(event.getExternalMovie().getExternalMovieId()).ifPresentOrElse(
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
                FindAndModifyOptions.options().returnNew(true),
                MovieDTO.class);
        notifySubscribers(updatedMovie);
    }

    @EventHandler
    public void handle(MovieDeletedEvent event) {
        movieRepository.deleteById(event.getMovieId());
    }

    @QueryHandler
    public List<MovieDTO> handle(GetNotSeenMoviesQuery query) {
        return movieRepository.findAllByWatchedFalse();
    }

    public List<MovieDTO> refreshMovies(List<RefreshData> moviesToRefresh) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MovieDTO.class);
        moviesToRefresh.forEach(refreshData -> {
            Query query = Query.query(Criteria.where(EXTERNAL_MOVIE_ID).is(refreshData.getMovieId()));
            Update update = Update.update(VOTE_AVERAGE_MDB, refreshData.getAverageVote())
                    .set(VOTE_COUNT, refreshData.getVoteCount())
                    .set(RELEASE_DATE_DIGITAL, refreshData.getDigitalReleaseDate());
            bulkOperations.updateOne(query, update);
        });

        BulkWriteResult result = bulkOperations.execute();
        log.info("BulkWriteResult = {}", result);

        return movieRepository.findAllByWatchedFalse();
    }

    private void handleMovieDuplicate() {
        notifySubscribers(new MovieDTO(MovieState.ALREADY_ADDED));
    }

    private void persistMovie(MovieSavedEvent event) {
        ExternalMovieInfo externalMovieInfo = event.getExternalMovie().getExternalMovieInfo();
        MovieDTO movieDTO = MovieDTO.builder()
                .aggregateId(event.getMovieId())
                .externalMovieId(event.getExternalMovie().getExternalMovieId())
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
                .watched(externalMovieInfo.isWatched())
                .build();
        movieRepository.insert(movieDTO);
        notifySubscribers(movieDTO);
    }

    private void notifySubscribers(MovieDTO movieDTO) {
        queryUpdateEmitter.emit(GetMovieQuery.class, query -> true, movieDTO);
    }
}
