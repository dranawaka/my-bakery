package com.aurelius.tech.mybakery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration that can be customized through environment variables.
 */
@Configuration
public class CorsConfig {

    @Value("${CORS_ALLOWED_ORIGINS:${cors.allowed-origins:http://localhost:3000,http://localhost:3001,https://*.railway.app,https://*.vercel.app,https://*.netlify.app}}")
    private String allowedOrigins;

    @Value("${CORS_ALLOWED_METHODS:${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}}")
    private String allowedMethods;

    @Value("${CORS_ALLOWED_HEADERS:${cors.allowed-headers:*}}")
    private String allowedHeaders;

    @Value("${CORS_EXPOSED_HEADERS:${cors.exposed-headers:x-auth-token,Authorization,Content-Type,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers}}")
    private String exposedHeaders;

    @Value("${CORS_ALLOW_CREDENTIALS:${cors.allow-credentials:true}}")
    private boolean allowCredentials;

    @Value("${CORS_MAX_AGE:${cors.max-age:3600}}")
    private long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parse allowed origins from comma-separated string
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        if (origins.contains("*") || origins.stream().anyMatch(origin -> origin.contains("*"))) {
            // If wildcard is present, use allowedOriginPatterns
            configuration.setAllowedOriginPatterns(origins);
        } else {
            // Otherwise use specific origins
            configuration.setAllowedOrigins(origins);
        }
        
        // Parse allowed methods
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        
        // Parse allowed headers
        if ("*".equals(allowedHeaders)) {
            configuration.setAllowedHeaders(Arrays.asList("*"));
        } else {
            configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        }
        
        // Parse exposed headers
        configuration.setExposedHeaders(Arrays.asList(exposedHeaders.split(",")));
        
        // Set credentials and max age
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
