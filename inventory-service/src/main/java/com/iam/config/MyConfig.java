package com.iam.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MyConfig {

    private final static String PRODUCT_SERVICE_URL="http://product-service/api/v1/products";

    //Add loadbalancer to WebClient.Builder and use builder to create webClient object
    @Bean
    @LoadBalanced
    WebClient.Builder builder() {
        return WebClient.builder();
    }

    @Bean
    WebClient getWebClient(WebClient.Builder builder) {
        //Create Web client to make calls to Product service
        WebClient webClient = builder
                .baseUrl(PRODUCT_SERVICE_URL)
                .build();
        return webClient;
    }
}
