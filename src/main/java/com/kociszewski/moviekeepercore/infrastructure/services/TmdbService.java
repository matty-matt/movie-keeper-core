package com.kociszewski.moviekeepercore.infrastructure.services;

import com.kociszewski.moviekeepercore.domain.DataService;
import com.kociszewski.moviekeepercore.domain.cast.Cast;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieId;
import com.kociszewski.moviekeepercore.domain.movie.info.MovieInfo;
import com.kociszewski.moviekeepercore.domain.movie.info.Title;
import com.kociszewski.moviekeepercore.domain.movie.info.Vote;
import com.kociszewski.moviekeepercore.domain.movie.info.releases.Releases;
import com.kociszewski.moviekeepercore.domain.trailers.TrailerSection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TmdbService implements DataService {

    public TmdbService(@Value("${api.key}") String apiKey) {
        System.out.println(apiKey);
    }

    @Override
    public MovieInfo findMovie(Title title) {
        // getDetails should be also called here
        return null;
    }

    @Override
    public Releases getReleases(MovieId movieId) {
        return null;
    }

    @Override
    public Vote getVote(MovieId movieId) {
        return null;
    }

    @Override
    public TrailerSection getTrailers(MovieId movieId) {
        return null;
    }

    @Override
    public Cast getCast(MovieId movieId) {
        return null;
    }
}
