package com.kociszewski.moviekeeper.domain.commands;

import com.kociszewski.moviekeeper.infrastructure.RefreshMovie;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

@Value
public class FetchRefreshData {
    @TargetAggregateIdentifier
    String proxyId;
    List<RefreshMovie> moviesToRefresh;
}