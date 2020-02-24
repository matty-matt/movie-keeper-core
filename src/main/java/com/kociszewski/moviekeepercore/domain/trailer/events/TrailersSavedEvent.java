package com.kociszewski.moviekeepercore.domain.trailer.events;

import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
import com.kociszewski.moviekeepercore.shared.model.MovieId;
import lombok.Value;

@Value
public class TrailersSavedEvent {
    private MovieId movieId;
    private TrailerSectionDTO trailerSectionDTO;
}
