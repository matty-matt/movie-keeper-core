package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.FindCastCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveCastCommand;
import com.kociszewski.moviekeeper.domain.events.CastDeletedEvent;
import com.kociszewski.moviekeeper.domain.events.CastFoundEvent;
import com.kociszewski.moviekeeper.domain.events.CastSavedEvent;
import com.kociszewski.moviekeeper.infrastructure.CastInfo;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.List;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;
import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@NoArgsConstructor
@Slf4j
public class CastAggregate {
    @AggregateIdentifier
    private String castId;
    private List<CastInfo> cast;

    @CommandHandler
    public CastAggregate(FindCastCommand command) {
        apply(new CastFoundEvent(command.getCastId(), command.getExternalMovieId()));
    }

    @EventSourcingHandler
    public void on(CastFoundEvent event) {
        this.castId = event.getCastId();
    }

    @CommandHandler
    public void handle(SaveCastCommand command) {
        if (cast.isEmpty()) {
            apply(new CastSavedEvent(command.getCastDTO()));
        }
    }

    @EventSourcingHandler
    public void on(CastSavedEvent event) {
        this.cast = event.getCastDTO().getCast()
                .stream()
                .map(dto -> CastInfo.builder()
                        .id(dto.getId())
                        .castId(dto.getCastId())
                        .character(dto.getCharacter())
                        .gender(dto.getGender())
                        .name(dto.getName())
                        .order(dto.getOrder())
                        .profilePath(dto.getProfilePath())
                        .build()).collect(Collectors.toList());
    }

    @EventSourcingHandler
    public void on(CastDeletedEvent event) {
        markDeleted();
    }
}
