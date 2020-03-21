package com.kociszewski.moviekeepercore.domain.movie;

import com.kociszewski.moviekeepercore.domain.CommonIntegrationSetup;
import com.kociszewski.moviekeepercore.domain.ExternalService;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastDTO;
import com.kociszewski.moviekeepercore.infrastructure.cast.CastRepository;
import com.kociszewski.moviekeepercore.infrastructure.movie.*;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerRepository;
import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
import com.kociszewski.moviekeepercore.shared.model.ExternalMovie;
import com.kociszewski.moviekeepercore.shared.model.SearchPhrase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MovieIntegrationTest extends CommonIntegrationSetup {

    private static final String SUPER_MOVIE = "SuperMovie";
    private static final String ANOTHER_SUPER_MOVIE = "AnotherSuperMovie";

    @MockBean
    private ExternalService externalService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TrailerRepository trailerRepository;

    @Autowired
    private CastRepository castRepository;

    @Test
    public void shouldStoreMovieTrailersAndCast() throws NotFoundInExternalServiceException {
        // given
        ExternalMovie generatedMovie = generateExternalMovie(SUPER_MOVIE);
        when(externalService.searchMovie(any())).thenReturn(generatedMovie);
        CastDTO castDTO = generateCast(generatedMovie.getExternalMovieId().getId());
        when(externalService.retrieveCast(any())).thenReturn(castDTO);
        TrailerSectionDTO trailerSectionDTO = generateTrailers(generatedMovie.getExternalMovieId().getId());
        when(externalService.retrieveTrailers(any())).thenReturn(trailerSectionDTO);

        // when
        ResponseEntity<MovieDTO> storedMovieResponse = storeMovie(SUPER_MOVIE);

        // then
        assertThat(storedMovieResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        MovieDTO body = storedMovieResponse.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getAggregateId()).isNotEmpty();

        Optional<MovieDTO> persistedMovie = movieRepository.findByExternalMovieId(body.getExternalMovieId());
        persistedMovie.ifPresent(movie -> {
            assertThat(movie).isEqualTo(body);
            assertThat(movie.getCreationDate()).isEqualTo(now);
            assertThat(movie.getLastRefreshDate()).isEqualTo(now);
            assertThat(movie.isWatched()).isFalse();
        });

        Optional<TrailerSectionDTO> persistedTrailers = trailerRepository.findByExternalMovieId(body.getExternalMovieId());
        persistedTrailers.ifPresent(trailers -> {
            assertThat(trailers).isEqualTo(trailerSectionDTO);
            assertThat(trailers.getMovieId()).isEqualTo(body.getAggregateId());
        });

        Optional<CastDTO> persistedCast = castRepository.findByExternalMovieId(body.getExternalMovieId());
        persistedCast.ifPresent(cast -> {
            assertThat(cast).isEqualTo(castDTO);
            assertThat(cast.getMovieId()).isEqualTo(body.getAggregateId());
        });

        // cleanup
        deleteMovie(body.getAggregateId());
    }

    @Test
    public void shouldDeleteMovieTrailersAndCast() throws NotFoundInExternalServiceException {
        // given
        ExternalMovie generatedMovie = generateExternalMovie(SUPER_MOVIE);
        when(externalService.searchMovie(any())).thenReturn(generatedMovie);
        when(externalService.retrieveCast(any())).thenReturn(generateCast(generatedMovie.getExternalMovieId().getId()));
        when(externalService.retrieveTrailers(any())).thenReturn(generateTrailers(generatedMovie.getExternalMovieId().getId()));
        ResponseEntity<MovieDTO> storedMovieResponse = storeMovie(SUPER_MOVIE);
        MovieDTO storedMovie = Objects.requireNonNull(storedMovieResponse.getBody());

        // when
        ResponseEntity<Void> deletedMovie = deleteMovie(storedMovie.getAggregateId());

        // then
        assertThat(deletedMovie.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Optional<MovieDTO> movie = movieRepository.findByExternalMovieId(storedMovie.getExternalMovieId());
        Optional<TrailerSectionDTO> trailers = trailerRepository.findByExternalMovieId(storedMovie.getExternalMovieId());
        Optional<CastDTO> cast = castRepository.findByExternalMovieId(storedMovie.getExternalMovieId());
        assertThat(movie).isNotPresent();
        assertThat(trailers).isNotPresent();
        assertThat(cast).isNotPresent();
    }

    @Test
    public void shouldMarkMovieAsWatched() throws NotFoundInExternalServiceException {
        // given
        ExternalMovie generatedMovie = generateExternalMovie(SUPER_MOVIE);
        when(externalService.searchMovie(any())).thenReturn(generatedMovie);
        when(externalService.retrieveCast(any())).thenReturn(generateCast(generatedMovie.getExternalMovieId().getId()));
        when(externalService.retrieveTrailers(any())).thenReturn(generateTrailers(generatedMovie.getExternalMovieId().getId()));

        ResponseEntity<MovieDTO> storedMovie = storeMovie(SUPER_MOVIE);
        HttpEntity<WatchedBody> httpEntity = new HttpEntity<>(new WatchedBody(true));

        // when
        ResponseEntity<MovieDTO> updatedMovieResponse = testRestTemplate
                .exchange(
                        String.format("http://localhost:%d/movies/%s", randomServerPort, Objects.requireNonNull(storedMovie.getBody()).getAggregateId()),
                        HttpMethod.PUT,
                        httpEntity,
                        MovieDTO.class);
        // then
        assertThat(updatedMovieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        MovieDTO response = updatedMovieResponse.getBody();
        assertThat(response).isNotNull();

        Optional<MovieDTO> persistedMovie = movieRepository.findByExternalMovieId(response.getExternalMovieId());
        persistedMovie.ifPresent(movie -> assertThat(movie.isWatched()).isTrue());

        // cleanup
        deleteMovie(response.getAggregateId());
    }

    @Test
    public void shouldGetMovieById() throws NotFoundInExternalServiceException {
        // given
        ExternalMovie generatedMovie = generateExternalMovie(SUPER_MOVIE);
        when(externalService.searchMovie(any())).thenReturn(generatedMovie);
        when(externalService.retrieveCast(any())).thenReturn(generateCast(generatedMovie.getExternalMovieId().getId()));
        when(externalService.retrieveTrailers(any())).thenReturn(generateTrailers(generatedMovie.getExternalMovieId().getId()));

        ResponseEntity<MovieDTO> storedMovieResponse = storeMovie(SUPER_MOVIE);
        MovieDTO storedMovie = Objects.requireNonNull(storedMovieResponse.getBody());

        // when
        ResponseEntity<MovieDTO> getMovieResponse = testRestTemplate
                .getForEntity(
                        String.format("http://localhost:%d/movies/%s", randomServerPort, storedMovie.getAggregateId()),
                        MovieDTO.class);

        // then
        assertThat(getMovieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        MovieDTO fetchedMovie = getMovieResponse.getBody();
        assertThat(fetchedMovie).isNotNull();
        assertThat(fetchedMovie).isEqualTo(storedMovie);

        // cleanup
        deleteMovie(storedMovie.getAggregateId());
    }

    @Test
    public void shouldGetAllMovies() throws NotFoundInExternalServiceException {
        // given
        ExternalMovie superMovie = generateExternalMovie(SUPER_MOVIE);
        ExternalMovie anotherSuperMovie = generateExternalMovie(ANOTHER_SUPER_MOVIE);
        when(externalService.searchMovie(new SearchPhrase(SUPER_MOVIE))).thenReturn(superMovie);
        when(externalService.searchMovie(new SearchPhrase(ANOTHER_SUPER_MOVIE))).thenReturn(anotherSuperMovie);
        when(externalService.retrieveCast(superMovie.getExternalMovieId()))
                .thenReturn(generateCast(superMovie.getExternalMovieId().getId()));
        when(externalService.retrieveTrailers(superMovie.getExternalMovieId()))
                .thenReturn(generateTrailers(superMovie.getExternalMovieId().getId()));
        when(externalService.retrieveCast(anotherSuperMovie.getExternalMovieId()))
                .thenReturn(generateCast(anotherSuperMovie.getExternalMovieId().getId()));
        when(externalService.retrieveTrailers(anotherSuperMovie.getExternalMovieId()))
                .thenReturn(generateTrailers(anotherSuperMovie.getExternalMovieId().getId()));

        ResponseEntity<MovieDTO> firstMovieResponse = storeMovie(SUPER_MOVIE);
        ResponseEntity<MovieDTO> secondMovieResponse = storeMovie(ANOTHER_SUPER_MOVIE);

        // when
        ResponseEntity<MovieDTO[]> getAllMoviesResponse = testRestTemplate
                .getForEntity(
                        String.format("http://localhost:%d/movies", randomServerPort), MovieDTO[].class);

        // then
        assertThat(getAllMoviesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<MovieDTO> movies = Arrays.asList(Objects.requireNonNull(getAllMoviesResponse.getBody()));
        assertThat(movies.size()).isEqualTo(2);
        assertThat(movies).containsExactly(firstMovieResponse.getBody(), secondMovieResponse.getBody());

        // cleanup
        deleteMovie(Objects.requireNonNull(firstMovieResponse.getBody()).getAggregateId());
        deleteMovie(Objects.requireNonNull(secondMovieResponse.getBody()).getAggregateId());
    }
}


