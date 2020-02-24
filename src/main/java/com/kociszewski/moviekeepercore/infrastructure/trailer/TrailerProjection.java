package com.kociszewski.moviekeepercore.infrastructure.trailer;

import com.kociszewski.moviekeepercore.domain.trailer.events.TrailersDeletedEvent;
import com.kociszewski.moviekeepercore.domain.trailer.events.TrailersSavedEvent;
import com.kociszewski.moviekeepercore.domain.trailer.queries.GetTrailersQuery;
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
        trailerRepository.findByExternalMovieId(event.getTrailerSectionDTO().getExternalMovieId()).ifPresentOrElse(
                foundTrailers -> skip(foundTrailers.getExternalMovieId()),
                () -> persistTrailers(event.getTrailerSectionDTO())
        );
    }

    @QueryHandler
    public List<TrailerDTO> handle(GetTrailersQuery query) {
        return trailerRepository.
                findById(query.getMovieId().getId())
                .orElseThrow(TrailersNotFoundException::new).getTrailers();
    }

    @EventHandler
    public void handle(TrailersDeletedEvent event) {
        trailerRepository.deleteById(event.getMovieId().getId());
    }

    private void skip(String movieId) {
        log.info("Skipping persisting trailers for movieId={}", movieId);
    }

    private void persistTrailers(TrailerSectionDTO trailers) {
        trailerRepository.insert(trailers);
    }
}
