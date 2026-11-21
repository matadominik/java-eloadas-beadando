package com.example.beadando;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ForexPageController {

    private final ForexApiClient forexApiClient;

    private final String[] instruments = {
            "EUR_USD", "GBP_USD", "USD_JPY", "USD_CHF",
            "AUD_USD", "USD_CAD", "EUR_GBP", "EUR_JPY"
    };

    private final String[] granularities = {
            "M1", "M5", "M15", "M30", "H1", "H4", "D"
    };

    public ForexPageController(ForexApiClient forexApiClient) {
        this.forexApiClient = forexApiClient;
    }

    @GetMapping("/forex/account")
    public String account(Model model) {
        try {
            String response = forexApiClient.getAccounts();

            List<String> accountIds = new ArrayList<>();
            Matcher matcher = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"")
                    .matcher(response);

            while (matcher.find()) {
                accountIds.add(matcher.group(1));
            }

            model.addAttribute("accountIds", accountIds);
            model.addAttribute("accountCount", accountIds.size());
            model.addAttribute("connected", true);
            model.addAttribute("error", null);

        } catch (Exception e) {
            model.addAttribute("accountIds", List.of());
            model.addAttribute("accountCount", 0);
            model.addAttribute("connected", false);
            model.addAttribute(
                    "error",
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Az OANDA API jelenleg nem érhető el."
            );
        }

        model.addAttribute("currentPage", "forex");
        return "forex_account";
    }

    @GetMapping("/forex/price")
    public String price(
            @RequestParam(defaultValue = "EUR_USD") String instrument,
            Model model
    ) {
        if (!Arrays.asList(instruments).contains(instrument)) {
            instrument = "EUR_USD";
        }

        model.addAttribute("instruments", instruments);
        model.addAttribute("selectedInstrument", instrument);

        try {
            model.addAttribute("priceData", forexApiClient.getPrice(instrument));
            model.addAttribute("error", null);
        } catch (Exception e) {
            model.addAttribute("priceData", null);
            model.addAttribute(
                    "error",
                    e.getMessage() != null
                            ? e.getMessage()
                            : "Az árfolyam lekérése sikertelen."
            );
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
        if (!Arrays.asList(instruments).contains(instrument)) {
            instrument = "EUR_USD";
        }

        if (!Arrays.asList(granularities).contains(granularity)) {
            granularity = "H1";
        }

        model.addAttribute("instruments", instruments);
        model.addAttribute("granularities", granularities);
        model.addAttribute("selectedInstrument", instrument);
        model.addAttribute("selectedGranularity", granularity);

        try {
            model.addAttribute(
                    "historyData",
                    forexApiClient.getHistory(instrument, granularity)
            );
            model.addAttribute("error", null);
        } catch (Exception e) {
            model.addAttribute("historyData", null);
            model.addAttribute(
                    "error",
                    e.getMessage() != null
                            ? e.getMessage()
                            : "A historikus árfolyamok lekérése sikertelen."
            );
        }

        model.addAttribute("currentPage", "forex");
        return "forex_history";
    }

    @GetMapping("/forex/open")
    public String open(Model model) {
        model.addAttribute("instruments", instruments);
        model.addAttribute("selectedInstrument", "EUR_USD");
        model.addAttribute("error", null);
        model.addAttribute("result", null);
        model.addAttribute("currentPage", "forex");
        return "forex_open";
    }

    @PostMapping("/forex/open")
    public String openPosition(
            @RequestParam String instrument,
            @RequestParam long units,
            Model model
    ) {
        model.addAttribute("instruments", instruments);
        model.addAttribute("selectedInstrument", instrument);

        if (!Arrays.asList(instruments).contains(instrument)) {
            model.addAttribute("error", "Érvénytelen devizapár.");
            model.addAttribute("result", null);
        } else if (units == 0) {
            model.addAttribute("error", "A mennyiség nem lehet nulla.");
            model.addAttribute("result", null);
        } else {
            try {
                model.addAttribute(
                        "result",
                        forexApiClient.openPosition(instrument, units)
                );
                model.addAttribute("error", null);
            } catch (Exception e) {
                model.addAttribute("result", null);
                model.addAttribute(
                        "error",
                        e.getMessage() != null
                                ? e.getMessage()
                                : "A pozíció megnyitása sikertelen."
                );
            }
        }

        model.addAttribute("currentPage", "forex");
        return "forex_open";
    }

    @GetMapping("/forex/positions")
    public String positions(Model model) {
        try {
            String response = forexApiClient.getOpenPositions();

            Matcher positionsMatcher = Pattern.compile(
                    "\"positions\"\\s*:\\s*(\\[.*?\\])",
                    Pattern.DOTALL
            ).matcher(response);

            String positionsData = "[]";
            boolean hasOpenPositions = false;
            int positionsCount = 0;

            if (positionsMatcher.find()) {
                positionsData = positionsMatcher.group(1).trim();

                if (!positionsData.equals("[]")) {
                    hasOpenPositions = true;

                    Matcher objectMatcher = Pattern.compile("\\{").matcher(positionsData);

                    while (objectMatcher.find()) {
                        positionsCount++;
                    }
                }
            }

            Matcher transactionMatcher = Pattern.compile(
                    "\"lastTransactionID\"\\s*:\\s*\"([^\"]+)\""
            ).matcher(response);

            String lastTransactionId = transactionMatcher.find()
                    ? transactionMatcher.group(1)
                    : "-";

            model.addAttribute("positionsData", positionsData);
            model.addAttribute("hasOpenPositions", hasOpenPositions);
            model.addAttribute("positionsCount", positionsCount);
            model.addAttribute("lastTransactionId", lastTransactionId);
            model.addAttribute("error", null);

        } catch (Exception e) {
            model.addAttribute("positionsData", null);
            model.addAttribute("hasOpenPositions", false);
            model.addAttribute("positionsCount", 0);
            model.addAttribute("lastTransactionId", "-");
            model.addAttribute(
                    "error",
                    e.getMessage() != null
                            ? e.getMessage()
                            : "A nyitott pozíciók lekérése sikertelen."
            );
        }

        model.addAttribute("currentPage", "forex");
        return "forex_positions";
    }

    @GetMapping("/forex/close")
    public String close(Model model) {
        model.addAttribute("tradeId", null);
        model.addAttribute("result", null);
        model.addAttribute("error", null);
        model.addAttribute("currentPage", "forex");
        return "forex_close";
    }

    @PostMapping("/forex/close")
    public String closeTrade(
            @RequestParam long tradeId,
            Model model
    ) {
        model.addAttribute("tradeId", tradeId);

        if (tradeId <= 0) {
            model.addAttribute("result", null);
            model.addAttribute("error", "A Trade ID pozitív szám kell legyen.");
        } else {
            try {
                model.addAttribute(
                        "result",
                        forexApiClient.closeTrade(tradeId)
                );
                model.addAttribute("error", null);
            } catch (Exception e) {
                model.addAttribute("result", null);
                model.addAttribute(
                        "error",
                        e.getMessage() != null
                                ? e.getMessage()
                                : "A pozíció lezárása sikertelen."
                );
            }
        }

        model.addAttribute("currentPage", "forex");
        return "forex_close";
    }
}