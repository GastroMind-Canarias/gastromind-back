package com.gastromind.api.application.services;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsualPurchaseQuantityMathTest {

    @Test
    void toCanonicalAmount_convertsGramsToKg() {
        assertEquals(
                0,
                UsualPurchaseQuantityMath.toCanonicalAmount(new BigDecimal("500"), "g")
                        .compareTo(new BigDecimal("0.5")));
    }

    @Test
    void median_odd() {
        assertEquals(
                new BigDecimal("2.0000"),
                UsualPurchaseQuantityMath.median(List.of(
                        BigDecimal.ONE, BigDecimal.valueOf(2), BigDecimal.valueOf(3))));
    }

    @Test
    void median_even() {
        assertEquals(
                new BigDecimal("2.5000"),
                UsualPurchaseQuantityMath.median(List.of(BigDecimal.ONE, BigDecimal.valueOf(4))));
    }

    @Test
    void presentationUnit_prefersKgWhenMassPresent() {
        assertEquals("kg", UsualPurchaseQuantityMath.presentationUnit("g"));
    }
}
