package com.kociszewski.moviekeeper.infrastructure;

import com.kociszewski.moviekeeper.domain.events.CastDeletedEvent;
import com.kociszewski.moviekeeper.domain.events.CastSavedEvent;
import com.kociszewski.moviekeeper.domain.queries.GetCastQuery;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class CastProjection {
    private final CastRepository castRepository;

    @EventHandler
    public void handle(CastSavedEvent event) {
        log.info("Handling {}", event.getClass().getSimpleName());
        castRepository.findByExternalMovieId(event.getCastDTO().getExternalMovieId()).ifPresentOrElse(
                foundCast -> skip(foundCast.getExternalMovieId()),
                () -> persistCast(event.getCastDTO())
        );
    }

    @QueryHandler
    public List<CastInfoDTO> handle(GetCastQuery query) {
        return castRepository
                .findByMovieId(query.getMovieId())
                .orElseThrow(CastNotFoundException::new).getCast();
    }

    @EventHandler
    public void handle(CastDeletedEvent event) {
        castRepository.deleteById(event.getCastEntityId());
    }

    private void skip(String movieId) {
        log.info("Skipping persisting cast for externalMovieId={}", movieId);
    }

    private void persistCast(CastDTO cast) {
        castRepository.insert(cast);
    }
}
