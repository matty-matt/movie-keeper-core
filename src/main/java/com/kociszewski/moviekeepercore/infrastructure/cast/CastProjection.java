package com.kociszewski.moviekeepercore.infrastructure.cast;

import com.kociszewski.moviekeepercore.domain.cast.events.CastDeletedEvent;
import com.kociszewski.moviekeepercore.domain.cast.events.CastSavedEvent;
import com.kociszewski.moviekeepercore.domain.cast.queries.GetCastQuery;
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
        castRepository.findByExternalMovieId(event.getCastDTO().getExternalMovieId()).ifPresentOrElse(
                foundCast -> skip(foundCast.getExternalMovieId()),
                () -> persistCast(event.getCastDTO())
        );
    }

    @QueryHandler
    public List<CastInfoDTO> handle(GetCastQuery query) {
        return castRepository
                .findByMovieId(query.getMovieId().getId())
                .orElseThrow(CastNotFoundException::new).getCast();
    }

    @EventHandler
    public void handle(CastDeletedEvent event) {
        castRepository.deleteById(event.getCastEntityId().getId());
    }

    private void skip(String movieId) {
        log.info("Skipping persisting cast for movieId={}", movieId);
    }

    private void persistCast(CastDTO cast) {
        castRepository.insert(cast);
    }
}
