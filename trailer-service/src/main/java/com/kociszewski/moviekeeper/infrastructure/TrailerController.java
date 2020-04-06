package com.kociszewski.moviekeeper.infrastructure;

import com.kociszewski.moviekeeper.domain.queries.GetTrailersQuery;
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
@RequestMapping("/movies/{movieId}/videos")
public class TrailerController {

    private final QueryGateway queryGateway;

    @GetMapping
    public List<TrailerDTO> trailers(@PathVariable("movieId") String movieId) {
        return queryGateway.query(
                new GetTrailersQuery(movieId),
                ResponseTypes.multipleInstancesOf(TrailerDTO.class)).join();
    }
}
