package com.kociszewski.trailerservice.domain;

import com.kociszewski.trailerservice.domain.commands.FindTrailersCommand;
import com.kociszewski.trailerservice.domain.commands.SaveTrailersCommand;
import com.kociszewski.trailerservice.domain.events.TrailersDeletedEvent;
import com.kociszewski.trailerservice.domain.events.TrailersFoundEvent;
import com.kociszewski.trailerservice.domain.events.TrailersSavedEvent;
import lombok.Data;
import lombok.Getter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.List;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;
import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@Getter
public class TrailerAggregate {
    @AggregateIdentifier
    private String trailerEntityId;
    private List<Trailer> trailers;

    private TrailerAggregate() {}

    @CommandHandler
    public TrailerAggregate(FindTrailersCommand command) {
        apply(new TrailersFoundEvent(command.getMovieId(), trailerEntityId, command.getExternalMovieId()));
    }

    @CommandHandler
    public void handle(SaveTrailersCommand command) {
        if (trailers.isEmpty()) {
            apply(new TrailersSavedEvent(command.getTrailerSectionDTO()));
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
        markDeleted();
    }
}
