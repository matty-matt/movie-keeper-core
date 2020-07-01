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
        // This prevents from replaying in ReleaseTrackerSaga
        var trackingProcessorConfig = TrackingEventProcessorConfiguration
                .forSingleThreadedProcessing()
                .andInitialTrackingToken(StreamableMessageSource::createHeadToken);
        configurer.registerTrackingEventProcessor("ReleaseTrackerSagaProcessor",
                org.axonframework.config.Configuration::eventStore,
                c -> trackingProcessorConfig);
    }
}
