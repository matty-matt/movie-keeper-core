package com.kociszewski.moviekeeper.integration;

import com.kociszewski.moviekeeper.infrastructure.ReleaseRefreshScheduler;
import com.kociszewski.moviekeeper.integration.common.TestContainersSetup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;


public class ReleaseTrackerSchedulerTest extends TestContainersSetup {

    @SpyBean
    private ReleaseRefreshScheduler releaseRefreshScheduler;

    @Test
    public void test() {
        await()
                .atMost(Duration.of(15L, ChronoUnit.SECONDS))
                .untilAsserted(() -> verify(releaseRefreshScheduler, atLeast(1)).refreshUnseenMovies());
    }
}
