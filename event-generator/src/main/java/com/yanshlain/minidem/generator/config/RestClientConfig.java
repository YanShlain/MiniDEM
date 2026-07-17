package com.yanshlain.minidem.generator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * @Configuration marks this class as a source of @Bean definitions -- Spring calls each
 * @Bean method once at startup and registers whatever it returns in the application
 * context, the same classpath-scanning discovery as @Component, just for beans you can't
 * annotate directly (you don't own RestClient's class to put @Component on it).
 *
 * RestClient is Spring's modern synchronous HTTP client (Boot 3.2+), the intended
 * replacement for the older RestTemplate -- closest analogs are .NET's HttpClient
 * (ideally via IHttpClientFactory) or Go's net/http client with a fluent builder.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient ingestRestClient(@Value("${minidem.ingest.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
