
package com.example.beadando;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ForexApiClient {

    private final RestClient restClient;
    private final String accountId;

    public ForexApiClient(
            @Value("${oanda.base-url}") String baseUrl,
            @Value("${oanda.token}") String token,
            @Value("${oanda.account-id}") String accountId
    ) {
        this.accountId = accountId;
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

    public String getPrice(String instrument) {
        return restClient.get()
                .uri("/v3/accounts/{accountId}/pricing?instruments={instrument}", accountId, instrument)
                .retrieve()
                .body(String.class);
    }
}
