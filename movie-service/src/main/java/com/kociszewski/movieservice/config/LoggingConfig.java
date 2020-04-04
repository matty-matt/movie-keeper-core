package com.kociszewski.movieservice.config;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.eventhandling.interceptors.EventLoggingInterceptor;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.interceptors.LoggingInterceptor;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Autowired
    public void commandInterceptor(@Autowired CommandBus commandBus) {
        commandBus.registerHandlerInterceptor(new LoggingInterceptor<>());
    }

    @Autowired
    public void eventInterceptor(@Autowired EventStore eventStore) {
        eventStore.registerDispatchInterceptor(new EventLoggingInterceptor());
    }

    @Autowired
    public void queryInterceptor(@Autowired QueryGateway queryGateway) {
        queryGateway.registerDispatchInterceptor(new LoggingInterceptor<>());
    }
}