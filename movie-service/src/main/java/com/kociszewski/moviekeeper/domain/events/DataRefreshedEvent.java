package com.kociszewski.moviekeeper.domain.events;

import com.kociszewski.moviekeeper.domain.info.Release;
import com.kociszewski.moviekeeper.infrastructure.Vote;
import lombok.Value;

@Value
public class DataRefreshedEvent {
    String movieId;
    Vote refreshedVote;
    Release refreshedRelease;
}
