package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.domain.info.Release;
import com.kociszewski.moviekeeper.infrastructure.Vote;
import lombok.Value;

@Value
public class MovieRefreshedEvent {
    String movieId;
    Vote refreshedVote;
    Release refreshedRelease;
}
