package com.example.beadando;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

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
        try {
            return restClient.get()
                    .uri("/v3/accounts/{accountId}/pricing?instruments={instrument}",
                            accountId, instrument)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientResponseException e) {
            throw new RuntimeException(
                    "OANDA HTTP " + e.getStatusCode().value()
                            + ": " + e.getResponseBodyAsString(),
                    e
            );
        }
    }

    public String getHistory(String instrument, String granularity) {
        return restClient.get()
                .uri("/v3/instruments/{instrument}/candles?granularity={granularity}&count=10&price=M",
                        instrument, granularity)
                .retrieve()
                .body(String.class);
    }

    public String getOpenPositions() {
        try {
            return restClient.get()
                    .uri("/v3/accounts/{accountId}/openPositions", accountId)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientResponseException e) {
            throw new RuntimeException(
                    "OANDA HTTP " + e.getStatusCode().value()
                            + ": " + e.getResponseBodyAsString(),
                    e
            );
        }
    }

    public String openPosition(String instrument, long units) {
        String orderJson = """
                {
                    "order": {
                        "type": "MARKET",
                        "instrument": "%s",
                        "units": "%d",
                        "timeInForce": "FOK",
                        "positionFill": "DEFAULT"
                    }
                }
                """.formatted(instrument, units);

        return restClient.post()
                .uri("/v3/accounts/{accountId}/orders", accountId)
                .body(orderJson)
                .retrieve()
                .body(String.class);
    }

    public String closeTrade(long tradeId) {
        return restClient.post()
                .uri("/v3/accounts/{accountId}/trades/{tradeId}/close",
                        accountId, tradeId)
                .retrieve()
                .body(String.class);
    }
}