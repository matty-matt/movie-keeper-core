package com.kociszewski.moviekeeper.domain.commands;

import com.kociszewski.moviekeeper.infrastructure.TrailerSectionDTO;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveTrailersCommand {
    @TargetAggregateIdentifier
    private String trailersId;
    private TrailerSectionDTO trailerSectionDTO;
}
