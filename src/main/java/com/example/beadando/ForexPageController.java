
package com.example.beadando;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/forex/price")
    public String price(
            @RequestParam(defaultValue = "EUR_USD") String instrument,
            Model model
    ) {
        model.addAttribute("instruments", new String[]{
                "EUR_USD", "GBP_USD", "USD_JPY", "USD_CHF",
                "AUD_USD", "USD_CAD", "EUR_GBP", "EUR_JPY"
        });

        model.addAttribute("selectedInstrument", instrument);

        try {
            model.addAttribute("priceData", forexApiClient.getPrice(instrument));
            model.addAttribute("error", null);
        } catch (Exception e) {
            model.addAttribute("priceData", null);
            model.addAttribute("error", "Az árfolyam lekérése sikertelen. Ellenőrizd az OANDA API hozzáférést.");
        }

        model.addAttribute("currentPage", "forex");
        return "forex_price";
    }
}
