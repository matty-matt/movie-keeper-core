package com.kociszewski.moviekeepercore.infrastructure.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchedBody {
    private boolean watched;
}
