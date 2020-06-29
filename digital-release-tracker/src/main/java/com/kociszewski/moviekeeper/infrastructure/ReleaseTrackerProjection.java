package com.kociszewski.moviekeeper.infrastructure;

import com.kociszewski.moviekeeper.domain.events.MoviesRefreshedEvent;
import com.kociszewski.moviekeeper.domain.queries.GetRefreshedMoviesQuery;
import com.mongodb.bulk.BulkWriteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.data.mongodb.core.BulkOperations;
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
public class ReleaseTrackerProjection {

    private static final String EXTERNAL_MOVIE_ID = "externalMovieId";
    private static final String VOTE_AVERAGE_MDB = "voteAverageMdb";
    private static final String VOTE_COUNT = "voteCount";
    private static final String RELEASE_DATE_DIGITAL = "releaseDateDigital";
    private final ReleaseTrackerRepository releaseTrackerRepository;
    private final MongoTemplate mongoTemplate;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @QueryHandler
    public List<MovieDTO> handle(GetRefreshedMoviesQuery query) {
        return releaseTrackerRepository.findAll();
    }

    @EventHandler
    public void handle(MoviesRefreshedEvent event) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MovieDTO.class);
        event.getRefreshedMovies().forEach(refreshData -> {
            Query query = Query.query(Criteria.where(EXTERNAL_MOVIE_ID).is(refreshData.getMovieId()));
            Update update = Update.update(VOTE_AVERAGE_MDB, refreshData.getAverageVote())
                    .set(VOTE_COUNT, refreshData.getVoteCount())
                    .set(RELEASE_DATE_DIGITAL, refreshData.getDigitalReleaseDate());
            bulkOperations.updateOne(query, update);
        });

        BulkWriteResult result = bulkOperations.execute();
        log.info("BulkWriteResult = {}", result);

        queryUpdateEmitter.emit(GetRefreshedMoviesQuery.class, query -> true, releaseTrackerRepository.findAllByWatchedFalse());
    }

    public List<RefreshMovie> findMoviesToRefresh() {
        return releaseTrackerRepository.findExternalMovieIdByWatchedFalse()
                .stream()
                .map(movie -> RefreshMovie
                        .builder()
                        .aggregateId(movie.getAggregateId())
                        .externalMovieId(movie.getExternalMovieId())
                        .build())
                .collect(Collectors.toList());
    }
}
