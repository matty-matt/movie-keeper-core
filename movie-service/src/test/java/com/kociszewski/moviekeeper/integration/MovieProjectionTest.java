package com.kociszewski.moviekeeper.integration;

import com.kociszewski.moviekeeper.infrastructure.MovieDTO;
import com.kociszewski.moviekeeper.infrastructure.MovieProjection;
import com.kociszewski.moviekeeper.infrastructure.RefreshData;
import com.kociszewski.moviekeeper.integration.common.CommonIntegrationSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MovieProjectionTest extends CommonIntegrationSetup {

    @Autowired
    private MovieProjection subject;

    @Test
    public void shouldRefreshMovies() {
        // given
        MovieDTO superMovie = storeMovie(SUPER_MOVIE).getBody();
        String refreshedSuperMovieReleaseDate = "2020-05-05T00:00";
        double refreshedSuperMovieVote = 9.0;
        int refreshedSuperMovieCount = 1000;

        assertThat(superMovie.getVoteCount()).isNotEqualTo(refreshedSuperMovieCount);
        assertThat(superMovie.getVoteAverageMdb()).isNotEqualTo(refreshedSuperMovieVote);
        assertThat(superMovie.getReleaseDateDigital()).isNotEqualTo(refreshedSuperMovieReleaseDate);

        MovieDTO anotherMovie = storeMovie(ANOTHER_SUPER_MOVIE).getBody();
        String anotherMovieReleaseDate = "2020-11-11T00:00";
        double refreshedAnotherMovieVote = 1.0;
        int refreshedAnotherMovieCount = 500;

        assertThat(anotherMovie.getVoteCount()).isNotEqualTo(refreshedAnotherMovieCount);
        assertThat(anotherMovie.getVoteAverageMdb()).isNotEqualTo(refreshedAnotherMovieVote);
        assertThat(anotherMovie.getReleaseDateDigital()).isNotEqualTo(anotherMovieReleaseDate);

        List<RefreshData> refreshableMovies = Arrays.asList(
                RefreshData.builder()
                        .movieId(superMovie.getExternalMovieId())
                        .averageVote(refreshedSuperMovieVote)
                        .voteCount(refreshedSuperMovieCount)
                        .digitalReleaseDate(refreshedSuperMovieReleaseDate)
                        .build(),
                RefreshData.builder()
                        .movieId(anotherMovie.getExternalMovieId())
                        .averageVote(refreshedAnotherMovieVote)
                        .voteCount(refreshedAnotherMovieCount)
                        .digitalReleaseDate(anotherMovieReleaseDate)
                        .build()
        );

        // when
        List<MovieDTO> refreshedMovies = subject.refreshMovies(refreshableMovies);

        // then
        MovieDTO refreshedSuperMovie = refreshedMovies.get(0);
        assertThat(refreshedSuperMovie.getVoteAverageMdb()).isEqualTo(refreshedSuperMovieVote);
        assertThat(refreshedSuperMovie.getVoteCount()).isEqualTo(refreshedSuperMovieCount);
        assertThat(refreshedSuperMovie.getReleaseDateDigital()).isEqualTo(refreshedSuperMovieReleaseDate);

        MovieDTO refreshedAnotherSuperMovie = refreshedMovies.get(1);
        assertThat(refreshedAnotherSuperMovie.getVoteAverageMdb()).isEqualTo(refreshedAnotherMovieVote);
        assertThat(refreshedAnotherSuperMovie.getVoteCount()).isEqualTo(refreshedAnotherMovieCount);
        assertThat(refreshedAnotherSuperMovie.getReleaseDateDigital()).isEqualTo(anotherMovieReleaseDate);
    }
}
