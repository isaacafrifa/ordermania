package com.iam.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MyBeans {

    //Create Web client to make calls to Product service
    private WebClient webClient;

    // url host won't be product-service cos we're calling from docker container
    private final static String PRODUCT_SERVICE_URL="http://host.docker.internal:8081/api/v1/products";

    @Bean
    //Create Web client to make calls to Product service
    public WebClient getWebClient(WebClient.Builder webClientBuilder) {
        this.webClient= webClientBuilder
                .baseUrl(PRODUCT_SERVICE_URL)
                .build();
        return webClient;
    }

}
