
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

    @GetMapping("/forex/history")
    public String history(
            @RequestParam(defaultValue = "EUR_USD") String instrument,
            @RequestParam(defaultValue = "H1") String granularity,
            Model model
    ) {
        String[] instruments = {
                "EUR_USD", "GBP_USD", "USD_JPY", "USD_CHF",
                "AUD_USD", "USD_CAD", "EUR_GBP", "EUR_JPY"
        };

        String[] granularities = {
                "M1", "M5", "M15", "M30", "H1", "H4", "D"
        };

        if (!java.util.Arrays.asList(instruments).contains(instrument)) {
            instrument = "EUR_USD";
        }

        if (!java.util.Arrays.asList(granularities).contains(granularity)) {
            granularity = "H1";
        }

        model.addAttribute("instruments", instruments);
        model.addAttribute("granularities", granularities);
        model.addAttribute("selectedInstrument", instrument);
        model.addAttribute("selectedGranularity", granularity);

        try {
            model.addAttribute("historyData", forexApiClient.getHistory(instrument, granularity));
            model.addAttribute("error", null);
        } catch (Exception e) {
            model.addAttribute("historyData", null);
            model.addAttribute("error", "A historikus árfolyamok lekérése sikertelen. Ellenőrizd az OANDA API hozzáférést.");
        }

        model.addAttribute("currentPage", "forex");
        return "forex_history";
    }
}
