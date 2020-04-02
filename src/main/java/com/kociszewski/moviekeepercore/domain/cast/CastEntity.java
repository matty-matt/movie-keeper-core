package com.kociszewski.moviekeepercore.domain.cast;

import com.kociszewski.moviekeepercore.domain.cast.commands.FindCastCommand;
import com.kociszewski.moviekeepercore.domain.cast.commands.SaveCastCommand;
import com.kociszewski.moviekeepercore.domain.cast.events.CastDeletedEvent;
import com.kociszewski.moviekeepercore.domain.cast.events.CastFoundEvent;
import com.kociszewski.moviekeepercore.domain.cast.events.CastSavedEvent;
import com.kociszewski.moviekeepercore.domain.trailer.commands.FindTrailersCommand;
import com.kociszewski.moviekeepercore.domain.trailer.commands.SaveTrailersCommand;
import com.kociszewski.moviekeepercore.domain.trailer.events.TrailersDeletedEvent;
import com.kociszewski.moviekeepercore.domain.trailer.events.TrailersFoundEvent;
import com.kociszewski.moviekeepercore.domain.trailer.events.TrailersSavedEvent;
import com.kociszewski.moviekeepercore.shared.model.CastEntityId;
import com.kociszewski.moviekeepercore.shared.model.CastInfo;
import com.kociszewski.moviekeepercore.shared.model.Trailer;
import com.kociszewski.moviekeepercore.shared.model.TrailerEntityId;
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
        apply(new CastFoundEvent(command.getMovieId(), castEntityId, command.getExternalMovieId()));
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
