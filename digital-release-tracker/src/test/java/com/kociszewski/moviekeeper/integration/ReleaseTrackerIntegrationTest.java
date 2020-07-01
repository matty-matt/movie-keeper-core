package com.kociszewski.moviekeeper.integration;

import com.kociszewski.moviekeeper.infrastructure.ReleaseRefreshScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Durations.TEN_SECONDS;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReleaseTrackerIntegrationTest {
//
//    @SpyBean
//    private ReleaseRefreshScheduler releaseRefreshScheduler;
//
//    @Test
//    public void test() {
//        await()
//                .atMost(TEN_SECONDS)
//                .untilAsserted(() -> verify(releaseRefreshScheduler, atLeast(1)).refreshUnseenMovies());
//    }
}
