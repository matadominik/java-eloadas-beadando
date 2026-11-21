package com.example.beadando;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalculatorService calculatorService;

    @Test
    void calculatorPageShouldLoad() throws Exception {
        mockMvc.perform(get("/calculator"))
                .andExpect(status().isOk())
                .andExpect(view().name("calculator"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Négy műveletes kalkulátor")));
    }

    @Test
    void additionShouldReturnResult() throws Exception {
        when(calculatorService.add(5, 3)).thenReturn(8.0);

        mockMvc.perform(post("/calculator")
                        .param("firstNumber", "5")
                        .param("secondNumber", "3")
                        .param("operation", "add"))
                .andExpect(status().isOk())
                .andExpect(view().name("calculator"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("8.0")));
    }

    @Test
    void subtractionShouldReturnResult() throws Exception {
        when(calculatorService.subtract(10, 4)).thenReturn(6.0);

        mockMvc.perform(post("/calculator")
                        .param("firstNumber", "10")
                        .param("secondNumber", "4")
                        .param("operation", "subtract"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("6.0")));
    }

    @Test
    void multiplicationShouldReturnResult() throws Exception {
        when(calculatorService.multiply(4, 5)).thenReturn(20.0);

        mockMvc.perform(post("/calculator")
                        .param("firstNumber", "4")
                        .param("secondNumber", "5")
                        .param("operation", "multiply"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("20.0")));
    }

    @Test
    void divisionShouldReturnResult() throws Exception {
        when(calculatorService.divide(10, 2)).thenReturn(5.0);

        mockMvc.perform(post("/calculator")
                        .param("firstNumber", "10")
                        .param("secondNumber", "2")
                        .param("operation", "divide"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("5.0")));
    }

    @Test
    void divisionByZeroShouldDisplayError() throws Exception {
        when(calculatorService.divide(5, 0))
                .thenThrow(new IllegalArgumentException("Nullával nem lehet osztani!"));

        mockMvc.perform(post("/calculator")
                        .param("firstNumber", "5")
                        .param("secondNumber", "0")
                        .param("operation", "divide"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nullával nem lehet osztani!")));
    }
}