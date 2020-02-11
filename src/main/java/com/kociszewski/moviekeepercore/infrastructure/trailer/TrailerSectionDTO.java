package com.kociszewski.moviekeepercore.infrastructure.trailer;

import lombok.Data;

import java.util.List;

@Data
public class TrailerSectionDTO {
    private String id;
    private List<TrailerDTO> results;
}
