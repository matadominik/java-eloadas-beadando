package com.example.beadando;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ForexApiClient {

    private final RestClient restClient;

    public ForexApiClient(
            @Value("${oanda.base-url}") String baseUrl,
            @Value("${oanda.token}") String token
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String getAccounts() {
        return restClient.get()
                .uri("/v3/accounts")
                .retrieve()
                .body(String.class);
    }
}