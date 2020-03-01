package com.kociszewski.moviekeepercore.domain.trailer.events;

import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
import lombok.Value;

@Value
public class TrailersSavedEvent {
    private TrailerSectionDTO trailerSectionDTO;
}
