package com.example.beadando;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForexPageController {

    private final ForexApiClient forexApiClient;

    public ForexPageController(ForexApiClient forexApiClient) {
        this.forexApiClient = forexApiClient;
    }

    @GetMapping("/forex/account")
    public String account(Model model) {
        try {
            model.addAttribute("accountData", forexApiClient.getAccounts());
            model.addAttribute("error", null);
        } catch (Exception e) {
            model.addAttribute("accountData", null);
            model.addAttribute("error", "Az OANDA API jelenleg nem érhető el. Ellenőrizd az API hozzáférést.");
        }

        model.addAttribute("currentPage", "forex");
        return "forex_account";
    }
}