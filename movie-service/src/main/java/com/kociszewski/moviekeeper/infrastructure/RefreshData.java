package com.kociszewski.moviekeeper.infrastructure;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RefreshData {
    String movieId;
    String aggregateId;
    double averageVote;
    long voteCount;
    String digitalReleaseDate;
}
