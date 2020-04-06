package com.kociszewski.moviekeeper.infrastructure;

import com.kociszewski.moviekeeper.domain.queries.GetCastQuery;
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
                new GetCastQuery(movieId),
                ResponseTypes.multipleInstancesOf(CastInfoDTO.class)).join();
    }
}
