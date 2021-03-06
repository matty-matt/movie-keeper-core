package com.kociszewski.moviekeeper.config;

import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.messaging.StreamableMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Autowired
    public void customTrackingConfig(EventProcessingConfigurer configurer) {
        var trackingProcessorConfig = TrackingEventProcessorConfiguration
                .forSingleThreadedProcessing()
                .andInitialTrackingToken(StreamableMessageSource::createHeadToken);

        // This prevents from replaying in MovieSaga
        configurer.registerTrackingEventProcessor("CastSagaProcessor",
                org.axonframework.config.Configuration::eventStore,
                c -> trackingProcessorConfig);
    }
}
