package com.kociszewski.moviekeepercore.infrastructure.cast;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies/{movieId}/cast")
public class CastController {

    private final CastService castService;

    @GetMapping
    public List<CastInfoDTO> getMovieCast(@PathVariable("movieId") String movieId) {
        return castService.movieCast(movieId).getCast();
    }
}
