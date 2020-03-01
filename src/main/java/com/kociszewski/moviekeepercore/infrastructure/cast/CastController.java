package com.kociszewski.moviekeepercore.infrastructure.cast;

import com.kociszewski.moviekeepercore.domain.cast.queries.GetCastQuery;
import com.kociszewski.moviekeepercore.shared.model.MovieId;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies/{movieId}/cast")
public class CastController {

    private final QueryGateway queryGateway;

    @GetMapping
    public List<CastInfoDTO> cast(@PathVariable("movieId") String movieId) {
        return queryGateway.query(
                new GetCastQuery(new MovieId(movieId)),
                ResponseTypes.multipleInstancesOf(CastInfoDTO.class)).join();
    }
}
