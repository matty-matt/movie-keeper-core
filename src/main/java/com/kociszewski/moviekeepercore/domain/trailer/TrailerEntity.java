package com.kociszewski.moviekeepercore.domain.trailer;

import com.kociszewski.moviekeepercore.domain.trailer.commands.SaveTrailersCommand;
import com.kociszewski.moviekeepercore.domain.trailer.events.TrailersDeletedEvent;
import com.kociszewski.moviekeepercore.domain.trailer.events.TrailersSavedEvent;
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
public class TrailerEntity {
    @EntityId
    private TrailerEntityId trailerEntityId;
    private List<Trailer> trailers;

    @CommandHandler
    public void handle(SaveTrailersCommand command) {
        apply(new TrailersSavedEvent(command.getTrailerSectionDTO()));
    }

    @EventSourcingHandler
    public void on(TrailersSavedEvent event) {
        this.trailers = event.getTrailerSectionDTO().getTrailers()
                .stream()
                .map(dto -> Trailer.builder()
                        .language(dto.getLanguage())
                        .country(dto.getCountry())
                        .key(dto.getKey())
                        .name(dto.getName())
                        .site(dto.getSite())
                        .type(dto.getType())
                        .size(dto.getSize()).build()).collect(Collectors.toList());
    }

    @EventSourcingHandler
    public void on(TrailersDeletedEvent event) {
        markDeleted();
    }
}
