package com.kociszewski.moviekeeper.infrastructure;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RefreshData {
    String movieId;
    double averageVote;
    long voteCount;
    String digitalReleaseDate;
}
