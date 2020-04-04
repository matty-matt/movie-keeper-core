package com.kociszewski.trailerservice.domain.commands;

import com.kociszewski.movieservice.infrastructure.trailer.TrailerSectionDTO;
import com.kociszewski.movieservice.shared.MovieId;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class SaveTrailersCommand {
    @TargetAggregateIdentifier
    private MovieId movieId;
    private TrailerSectionDTO trailerSectionDTO;
}
