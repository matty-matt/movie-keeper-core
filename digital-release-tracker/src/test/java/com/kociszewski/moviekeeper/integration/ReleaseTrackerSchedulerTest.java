package com.kociszewski.moviekeeper.integration;

import com.kociszewski.moviekeeper.infrastructure.ReleaseRefreshScheduler;
import com.kociszewski.moviekeeper.integration.common.TestContainersSetup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Durations.TEN_SECONDS;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;


public class ReleaseTrackerSchedulerTest extends TestContainersSetup {

    @SpyBean
    private ReleaseRefreshScheduler releaseRefreshScheduler;

    @Test
    public void test() {
        await()
                .atMost(TEN_SECONDS)
                .untilAsserted(() -> verify(releaseRefreshScheduler, atLeast(1)).refreshUnseenMovies());
    }
}
