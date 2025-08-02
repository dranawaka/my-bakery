package com.aurelius.tech.mybakery.config;

import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for Jackson serialization.
 * This configuration:
 * 1. Increases the maximum nesting depth to prevent exceptions in case of deep object graphs
 * 2. Disables FAIL_ON_EMPTY_BEANS to handle Hibernate proxies during serialization
 * 3. Configures proper handling of Java 8 date/time types
 */
@Configuration
public class JacksonNestingConfig {

    /**
     * Configures the ObjectMapper with increased nesting depth and support for Hibernate proxies.
     * 
     * @return the configured ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapperWithIncreasedNestingDepth() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Register JavaTimeModule to handle Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule());
        
        // Disable SerializationFeature.FAIL_ON_EMPTY_BEANS to prevent exceptions with Hibernate proxies
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        // Increase the maximum nesting depth from the default 1000 to 2000
        // This is a fallback measure in case any circular references are missed
        StreamWriteConstraints streamWriteConstraints = StreamWriteConstraints.builder()
                .maxNestingDepth(2000)
                .build();
        
        objectMapper.getFactory().setStreamWriteConstraints(streamWriteConstraints);
        
        return objectMapper;
    }
}