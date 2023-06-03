package com.phonecompany.billing;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExampleBillCalculatorTest {

    ExampleBillCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ExampleBillCalculator();
    }

    @Test
    @DisplayName("Should calculate for single number")
    void testSingle() {
        String callList = "420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57";
        assertEquals(new BigDecimal(0), calculator.calculate(callList));
    }

    @Test
    @DisplayName("Should calculate for multiple numbers")
    void testMultiple() {
        String callList = "420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57\n" +
                "420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00\n" +
                "420776562355,18-01-2020 08:59:20,18-01-2020 19:10:00\n" +
                "420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00\n" +
                "420776562352,18-01-2020 08:59:20,18-01-2020 09:10:00\n" +
                "420776562352,18-01-2020 08:59:20,18-01-2020 09:10:00\n" +
                "420776562352,18-01-2020 08:59:20,18-01-2020 09:10:00";
        assertEquals(new BigDecimal(758.10).setScale(2, RoundingMode.DOWN), calculator.calculate(callList));
    }


}