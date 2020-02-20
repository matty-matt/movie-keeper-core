package com.kociszewski.moviekeepercore.infrastructure.trailer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies/{movieId}/videos")
public class TrailerController {

    private final TrailerService trailerService;

    @GetMapping
    public List<TrailerDTO> trailers(@PathVariable("movieId") String movieId) {
        return trailerService.trailers(movieId).getTrailers();
    }
}
