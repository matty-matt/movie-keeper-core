package com.kociszewski.moviekeepercore;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.messaging.interceptors.LoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Autowired
    public void commandInterceptor(@Autowired CommandBus commandBus) {
        commandBus.registerHandlerInterceptor(new LoggingInterceptor<>());
    }
}