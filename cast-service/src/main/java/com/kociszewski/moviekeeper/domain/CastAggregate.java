package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.DeleteCastCommand;
import com.kociszewski.moviekeeper.domain.commands.CreateCastCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveCastCommand;
import com.kociszewski.moviekeeper.domain.events.CastDeletedEvent;
import com.kociszewski.moviekeeper.domain.events.CastCreatedEvent;
import com.kociszewski.moviekeeper.domain.events.CastSavedEvent;
import com.kociszewski.moviekeeper.infrastructure.CastInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;
import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@NoArgsConstructor
@Slf4j
@Getter
public class CastAggregate {
    @AggregateIdentifier
    private String castId;
    private List<CastInfo> cast;

    @CommandHandler
    public CastAggregate(CreateCastCommand command) {
        apply(new CastCreatedEvent(command.getCastId(), command.getMovieId(), command.getExternalMovieId()));
    }

    @EventSourcingHandler
    public void on(CastCreatedEvent event) {
        this.castId = event.getCastId();
        this.cast = new ArrayList<>();
    }

    @CommandHandler
    public void handle(SaveCastCommand command) {
        if (cast.isEmpty()) {
            apply(new CastSavedEvent(command.getCastId(), command.getCastDTO()));
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

    @CommandHandler
    public void handle(DeleteCastCommand command) {
        apply(new CastDeletedEvent(command.getCastId()));
    }

    @EventSourcingHandler
    public void on(CastDeletedEvent event) {
        markDeleted();
    }
}
