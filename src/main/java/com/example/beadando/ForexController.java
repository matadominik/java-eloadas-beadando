package com.example.beadando;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForexController {

    private final ForexApiClient forexApiClient;

    public ForexController(ForexApiClient forexApiClient) {
        this.forexApiClient = forexApiClient;
    }

    @GetMapping("/forex/test")
    public String testConnection() {
        return forexApiClient.getAccounts();
    }
}