package com.kociszewski.moviekeeper.domain.commands;

import com.kociszewski.moviekeeper.infrastructure.CastDTO;
import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SaveCastCommand {
    @TargetAggregateIdentifier
    String movieId;
    CastDTO castDTO;
}
