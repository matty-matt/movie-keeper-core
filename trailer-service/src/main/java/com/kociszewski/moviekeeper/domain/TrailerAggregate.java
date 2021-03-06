package com.kociszewski.moviekeeper.domain;

import com.kociszewski.moviekeeper.domain.commands.DeleteTrailersCommand;
import com.kociszewski.moviekeeper.domain.commands.CreateTrailersCommand;
import com.kociszewski.moviekeeper.domain.commands.SaveTrailersCommand;
import com.kociszewski.moviekeeper.domain.events.TrailersDeletedEvent;
import com.kociszewski.moviekeeper.domain.events.TrailersCreatedEvent;
import com.kociszewski.moviekeeper.domain.events.TrailersSavedEvent;
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
public class TrailerAggregate {
    @AggregateIdentifier
    private String trailersId;
    private List<Trailer> trailers;

    @CommandHandler
    public TrailerAggregate(CreateTrailersCommand command) {
        apply(new TrailersCreatedEvent(command.getTrailersId(), command.getMovieId(), command.getExternalMovieId()));
    }

    @EventSourcingHandler
    public void on(TrailersCreatedEvent event) {
        this.trailersId = event.getTrailersId();
        this.trailers = new ArrayList<>();
    }

    @CommandHandler
    public void handle(SaveTrailersCommand command) {
        if (trailers.isEmpty()) {
            apply(new TrailersSavedEvent(command.getTrailersId(), command.getTrailerSectionDTO()));
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

    @CommandHandler
    public void handle(DeleteTrailersCommand command) {
        apply(new TrailersDeletedEvent(command.getTrailersId()));
    }

    @EventSourcingHandler
    public void on(TrailersDeletedEvent event) {
        markDeleted();
    }
}
