package com.gastromind.api.application.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StoreNameNormalizerTest {
    private final StoreNameNormalizer normalizer = new StoreNameNormalizer();

    @Test
    void normalize_shouldReduceCompanySuffixes() {
        assertEquals("lidl", normalizer.normalize("LIDL SUPERMERCADOS S.A.U."));
    }
}
