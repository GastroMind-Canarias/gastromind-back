package com.gastromind.api.application.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Normaliza cantidades de ticket a unidades base (kg, l, ud) para comparar y mediana.
 */
public final class UsualPurchaseQuantityMath {

    private UsualPurchaseQuantityMath() {
    }

    public static BigDecimal toCanonicalAmount(BigDecimal quantityRaw, String canonicalUnitCode) {
        if (quantityRaw == null) {
            return BigDecimal.ZERO;
        }
        String c = canonicalUnitCode == null ? "ud" : canonicalUnitCode;
        return switch (c) {
            case "g" -> quantityRaw.divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP);
            case "ml" -> quantityRaw.divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP);
            default -> quantityRaw;
        };
    }

    /**
     * Unidad de presentación para la API: kg, l o ud (g/ml se convierten arriba).
     */
    public static String presentationUnit(String canonicalUnitCode) {
        String c = canonicalUnitCode == null ? "ud" : canonicalUnitCode;
        return switch (c) {
            case "g", "kg" -> "kg";
            case "ml", "l" -> "l";
            default -> "ud";
        };
    }

    public static BigDecimal median(List<BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            return BigDecimal.ZERO;
        }
        List<BigDecimal> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int n = sorted.size();
        if (n % 2 == 1) {
            return sorted.get(n / 2).setScale(4, RoundingMode.HALF_UP);
        }
        return sorted.get(n / 2 - 1)
                .add(sorted.get(n / 2))
                .divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_UP);
    }
}
