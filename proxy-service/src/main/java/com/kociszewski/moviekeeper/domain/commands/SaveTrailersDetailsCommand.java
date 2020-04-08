package com.kociszewski.moviekeeper.domain.commands;

import com.kociszewski.moviekeeper.infrastructure.TrailerSectionDTO;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveTrailersDetailsCommand {
    @TargetAggregateIdentifier
    private String proxyId;
    private TrailerSectionDTO trailerSectionDTO;
}
