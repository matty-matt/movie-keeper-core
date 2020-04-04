package com.kociszewski.trailerservice.domain;

import com.kociszewski.trailerservice.domain.commands.FindTrailersCommand;
import com.kociszewski.trailerservice.domain.commands.SaveTrailersCommand;
import com.kociszewski.trailerservice.domain.events.TrailersDeletedEvent;
import com.kociszewski.trailerservice.domain.events.TrailersFoundEvent;
import com.kociszewski.trailerservice.domain.events.TrailersSavedEvent;
import com.kociszewski.movieservice.shared.Trailer;
import com.kociszewski.movieservice.shared.TrailerEntityId;
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
    public void handle(FindTrailersCommand command) {
        AggregateLifecycle.apply(new TrailersFoundEvent(command.getMovieId(), trailerEntityId, command.getExternalMovieId()));
    }

    @CommandHandler
    public void handle(SaveTrailersCommand command) {
        if (trailers.isEmpty()) {
            AggregateLifecycle.apply(new TrailersSavedEvent(command.getTrailerSectionDTO()));
        }
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
        AggregateLifecycle.markDeleted();
    }
}
