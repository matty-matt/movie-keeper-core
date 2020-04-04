package com.kociszewski.trailerservice.infrastructure;

import com.kociszewski.trailerservice.domain.events.TrailersDeletedEvent;
import com.kociszewski.trailerservice.domain.events.TrailersSavedEvent;
import com.kociszewski.trailerservice.domain.queries.GetTrailersQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class TrailerProjection {
    private final TrailerRepository trailerRepository;

    @EventHandler
    public void handle(TrailersSavedEvent event) {
        log.info("Handling {}", event.getClass().getSimpleName());
        trailerRepository.findByExternalMovieId(event.getTrailerSectionDTO().getExternalMovieId()).ifPresentOrElse(
                foundTrailers -> skip(foundTrailers.getExternalMovieId()),
                () -> persistTrailers(event.getTrailerSectionDTO())
        );
    }

    @QueryHandler
    public List<TrailerDTO> handle(GetTrailersQuery query) {
        return trailerRepository.
                findByMovieId(query.getMovieId().getId())
                .orElseThrow(TrailersNotFoundException::new).getTrailers();
    }

    @EventHandler
    public void handle(TrailersDeletedEvent event) {
        trailerRepository.deleteById(event.getTrailerEntityId().getId());
    }

    private void skip(String movieId) {
        log.info("Skipping persisting trailers for externalMovieId={}", movieId);
    }

    private void persistTrailers(TrailerSectionDTO trailers) {
        trailerRepository.insert(trailers);
    }
}
