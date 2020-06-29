package com.kociszewski.moviekeeper.integration;

import com.kociszewski.moviekeeper.infrastructure.MovieDTO;
import com.kociszewski.moviekeeper.infrastructure.MovieRepository;
import com.kociszewski.moviekeeper.infrastructure.WatchedBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Durations.FIVE_SECONDS;
import static org.awaitility.Durations.ONE_HUNDRED_MILLISECONDS;

public class MovieIntegrationTest extends CommonIntegrationSetup {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    public void shouldStoreMovie() {
        // when
        ResponseEntity<MovieDTO> storedMovieResponse = storeMovie(SUPER_MOVIE);

        assertThat(storedMovieResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        MovieDTO body = storedMovieResponse.getBody();
        // then
        assertThat(body).isNotNull();
        assertThat(body.getAggregateId()).isNotEmpty();

        Optional<MovieDTO> persistedMovie = movieRepository.findByExternalMovieId(body.getExternalMovieId());
        persistedMovie.ifPresent(movie -> {
            assertThat(movie).isEqualTo(body);
            assertThat(movie.getCreationDate()).isEqualTo(now);
            assertThat(movie.isWatched()).isFalse();
        });
    }

    @Test
    public void shouldDeleteMovie() {
        // given
        ResponseEntity<MovieDTO> storedMovieResponse = storeMovie(SUPER_MOVIE);
        MovieDTO storedMovie = Objects.requireNonNull(storedMovieResponse.getBody());

        // when
        ResponseEntity<Void> deletedMovie = deleteMovie(storedMovie.getAggregateId());

        // then
        assertThat(deletedMovie.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        await()
                .atMost(FIVE_SECONDS)
                .with()
                .pollInterval(ONE_HUNDRED_MILLISECONDS)
                .until(() ->
                        movieRepository.findByExternalMovieId(storedMovie.getExternalMovieId()).isEmpty());
    }

    @Test
    public void shouldMarkMovieAsWatched() {
        // given
        ResponseEntity<MovieDTO> storedMovie = storeMovie(SUPER_MOVIE);
        HttpEntity<WatchedBody> httpEntity = new HttpEntity<>(new WatchedBody(true));

        // when
        ResponseEntity<MovieDTO> updatedMovieResponse = testRestTemplate
                .exchange(
                        String.format(ALTER_MOVIE_URL, randomServerPort, Objects.requireNonNull(storedMovie.getBody()).getAggregateId()),
                        HttpMethod.PUT,
                        httpEntity,
                        MovieDTO.class);
        // then
        assertThat(updatedMovieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        MovieDTO response = updatedMovieResponse.getBody();
        assertThat(response).isNotNull();

        Optional<MovieDTO> persistedMovie = movieRepository.findByExternalMovieId(response.getExternalMovieId());
        persistedMovie.ifPresent(movie -> assertThat(movie.isWatched()).isTrue());
    }

    @Test
    public void shouldGetMovieById() {
        // given
        ResponseEntity<MovieDTO> storedMovieResponse = storeMovie(SUPER_MOVIE);
        MovieDTO storedMovie = Objects.requireNonNull(storedMovieResponse.getBody());

        // when
        ResponseEntity<MovieDTO> getMovieResponse = testRestTemplate
                .getForEntity(
                        String.format(ALTER_MOVIE_URL, randomServerPort, storedMovie.getAggregateId()),
                        MovieDTO.class);

        // then
        assertThat(getMovieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        MovieDTO fetchedMovie = getMovieResponse.getBody();
        assertThat(fetchedMovie).isNotNull();
        assertThat(fetchedMovie).isEqualTo(storedMovie);
    }

    @Test
    public void shouldGetAllMovies() {
        // given
        ResponseEntity<MovieDTO> firstMovieResponse = storeMovie(SUPER_MOVIE);
        ResponseEntity<MovieDTO> secondMovieResponse = storeMovie(ANOTHER_SUPER_MOVIE);

        // when
        ResponseEntity<MovieDTO[]> getAllMoviesResponse = testRestTemplate
                .getForEntity(
                        String.format(GET_OR_POST_MOVIES, randomServerPort), MovieDTO[].class);

        // then
        assertThat(getAllMoviesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<MovieDTO> movies = Arrays.asList(Objects.requireNonNull(getAllMoviesResponse.getBody()));
        assertThat(movies.size()).isEqualTo(2);
        assertThat(movies).containsExactly(firstMovieResponse.getBody(), secondMovieResponse.getBody());
    }

    @Test
    public void shouldNotSearchForTrailersAndCastWhenMovieAlreadyOnList() {
        // given
        ResponseEntity<MovieDTO> firstMovieResponse = storeMovie(SUPER_MOVIE);
        String movieId = Objects.requireNonNull(firstMovieResponse.getBody()).getAggregateId();

        await()
                .atMost(FIVE_SECONDS)
                .with()
                .pollInterval(ONE_HUNDRED_MILLISECONDS)
                .until(() -> movieRepository.findById(movieId).isPresent());

        // when
        ResponseEntity<MovieDTO> secondMovieResponse = storeMovie(SUPER_MOVIE);

        // then
        assertThat(secondMovieResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}


