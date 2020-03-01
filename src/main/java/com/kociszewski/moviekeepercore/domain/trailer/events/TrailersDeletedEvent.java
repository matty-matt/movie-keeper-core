package com.kociszewski.moviekeepercore.domain.trailer.events;

import com.kociszewski.moviekeepercore.shared.model.TrailerEntityId;
import lombok.Value;

@Value
public class TrailersDeletedEvent {
    private TrailerEntityId trailerEntityId;
}
