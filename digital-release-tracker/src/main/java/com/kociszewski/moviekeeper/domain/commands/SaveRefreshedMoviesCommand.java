package com.kociszewski.moviekeeper.domain.commands;

import com.kociszewski.moviekeeper.infrastructure.RefreshData;
import lombok.Value;

import java.util.List;

@Value
public class SaveRefreshedMoviesCommand {
    String refreshId;
    List<RefreshData> refreshedMovies;
}
