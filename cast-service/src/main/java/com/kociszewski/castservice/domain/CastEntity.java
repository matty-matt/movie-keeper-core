package com.kociszewski.castservice.domain;

import com.kociszewski.castservice.domain.commands.FindCastCommand;
import com.kociszewski.castservice.domain.commands.SaveCastCommand;
import com.kociszewski.castservice.domain.events.CastDeletedEvent;
import com.kociszewski.castservice.domain.events.CastFoundEvent;
import com.kociszewski.castservice.domain.events.CastSavedEvent;
import com.kociszewski.movieservice.shared.CastEntityId;
import com.kociszewski.movieservice.shared.CastInfo;
import lombok.Builder;
import lombok.Data;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.EntityId;

import java.util.List;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;
import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Data
@Builder
public class CastEntity {
    @EntityId
    private CastEntityId castEntityId;
    private List<CastInfo> cast;

    @CommandHandler
    public void handle(FindCastCommand command) {
        AggregateLifecycle.apply(new CastFoundEvent(command.getMovieId(), castEntityId, command.getExternalMovieId()));
    }

    @CommandHandler
    public void handle(SaveCastCommand command) {
        if (cast.isEmpty()) {
            AggregateLifecycle.apply(new CastSavedEvent(command.getCastDTO()));
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
        AggregateLifecycle.markDeleted();
    }
}
