package com.aurelius.tech.mybakery.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Configuration class for Jackson serialization.
 * This configuration handles Hibernate proxy objects during JSON serialization.
 */
@Configuration
public class JacksonConfig {

    /**
     * Configures the ObjectMapper to handle Hibernate proxies.
     * 
     * @param builder the Jackson2ObjectMapperBuilder
     * @return the configured ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        
        // Disable FAIL_ON_EMPTY_BEANS to handle Hibernate proxies
        // This is mentioned in the error message: "to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS"
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        return objectMapper;
    }
}