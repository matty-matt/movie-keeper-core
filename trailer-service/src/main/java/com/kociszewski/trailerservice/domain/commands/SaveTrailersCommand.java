package com.kociszewski.trailerservice.domain.commands;

import com.kociszewski.trailerservice.infrastructure.TrailerSectionDTO;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveTrailersCommand {
    @TargetAggregateIdentifier
    private String movieId;
    private TrailerSectionDTO trailerSectionDTO;
}
