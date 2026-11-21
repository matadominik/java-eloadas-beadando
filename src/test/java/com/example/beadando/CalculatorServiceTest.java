package com.example.beadando;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorServiceTest {

    private final CalculatorService calculatorService = new CalculatorService();

    @Test
    void addShouldReturnCorrectResult() {
        assertEquals(8, calculatorService.add(5, 3));
    }

    @Test
    void subtractShouldReturnCorrectResult() {
        assertEquals(2, calculatorService.subtract(5, 3));
    }

    @Test
    void multiplyShouldReturnCorrectResult() {
        assertEquals(15, calculatorService.multiply(5, 3));
    }

    @Test
    void divideShouldReturnCorrectResult() {
        assertEquals(2, calculatorService.divide(6, 3));
    }

    @Test
    void divideByZeroShouldThrowException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculatorService.divide(5, 0)
        );
    }
}