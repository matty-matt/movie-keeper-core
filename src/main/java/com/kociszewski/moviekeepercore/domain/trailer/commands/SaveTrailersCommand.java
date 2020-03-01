package com.kociszewski.moviekeepercore.domain.trailer.commands;

import com.kociszewski.moviekeepercore.infrastructure.trailer.TrailerSectionDTO;
import com.kociszewski.moviekeepercore.shared.model.MovieId;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveTrailersCommand {
    @TargetAggregateIdentifier
    private MovieId movieId;
    private TrailerSectionDTO trailerSectionDTO;
}
