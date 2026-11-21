package com.example.beadando;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/calculator")
    public String calculator(Model model) {
        model.addAttribute("currentPage", "calculator");
        model.addAttribute("result", null);
        model.addAttribute("error", null);
        return "calculator";
    }

    @PostMapping("/calculator")
    public String calculate(
            @RequestParam double firstNumber,
            @RequestParam double secondNumber,
            @RequestParam String operation,
            Model model
    ) {
        model.addAttribute("currentPage", "calculator");
        model.addAttribute("firstNumber", firstNumber);
        model.addAttribute("secondNumber", secondNumber);
        model.addAttribute("operation", operation);

        try {
            double result;

            switch (operation) {
                case "add" -> result = calculatorService.add(firstNumber, secondNumber);
                case "subtract" -> result = calculatorService.subtract(firstNumber, secondNumber);
                case "multiply" -> result = calculatorService.multiply(firstNumber, secondNumber);
                case "divide" -> result = calculatorService.divide(firstNumber, secondNumber);
                default -> throw new IllegalArgumentException("Érvénytelen művelet.");
            }

            model.addAttribute("result", result);
            model.addAttribute("error", null);
        } catch (Exception e) {
            model.addAttribute("result", null);
            model.addAttribute("error", e.getMessage());
        }

        return "calculator";
    }
}