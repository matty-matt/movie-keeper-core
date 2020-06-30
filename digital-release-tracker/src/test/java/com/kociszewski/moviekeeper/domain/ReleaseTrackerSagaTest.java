package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.infrastructure.RefreshData;
import com.kociszewski.moviekeeper.infrastructure.RefreshMovie;
import org.axonframework.test.saga.SagaTestFixture;

import java.util.List;

public class ReleaseTrackerSagaTest {

    private SagaTestFixture<ReleaseTrackerSaga> fixture;
    private String refreshId;
    private String proxyId;
    private String movieId;
    private List<RefreshMovie> refreshMovies;
    private List<RefreshData> refreshData;

//    @BeforeEach
//    public void setup() {
//        fixture = new SagaTestFixture<>(ReleaseTrackerSaga.class);
//        fixture.registerCommandGateway(CommandGateway.class);
//        this.refreshId = UUID.randomUUID().toString();
//        this.movieId = UUID.randomUUID().toString();
//        this.proxyId = "proxy_".concat(refreshId);
//        String externalMovieId = "123";
//        this.refreshMovies = Collections.singletonList(
//                RefreshMovie.builder().externalMovieId(externalMovieId).aggregateId(refreshId).build());
//        this.refreshData = Collections.singletonList(RefreshData
//                .builder()
//                .digitalReleaseDate("2020-07-01T00:00")
//                .voteCount(10000)
//                .aggregateId(movieId)
//                .movieId(externalMovieId)
//                .averageVote(6.6)
//                .build());
//    }
//
//    @Test
//    public void shouldDispatchRefreshMoviesCommand() {
//        fixture.givenAggregate(refreshId)
//                .published()
//                .whenAggregate(refreshId)
//                .publishes(new RefreshMoviesDelegatedEvent(refreshId, refreshMovies))
//                .expectActiveSagas(1)
//                .expectDispatchedCommands(new RefreshMoviesCommand(proxyId, refreshMovies));
//    }
//
//    @Test
//    public void shouldDispatchSaveRefreshedMoviesCommand() {
//        fixture.givenAggregate(refreshId)
//                .published(new RefreshMoviesDelegatedEvent(refreshId, refreshMovies))
//                .whenAggregate(proxyId)
//                .publishes(new MoviesRefreshDataEvent(proxyId, refreshData))
//                .expectActiveSagas(0)
//                .expectDispatchedCommands(
//                        new SaveRefreshedMoviesCommand(refreshId, refreshData),
//                        new UpdateRefreshDataCommand(movieId, refreshData.get(0)));
//    }
}
