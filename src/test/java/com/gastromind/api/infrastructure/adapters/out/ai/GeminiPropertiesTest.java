package com.gastromind.api.infrastructure.adapters.out.ai;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeminiPropertiesTest {

    @Test
    void getModelAttemptOrder_primaryThenFallbacks_dedupes() {
        GeminiProperties p = new GeminiProperties();
        p.setModel("gemini-2.5-flash");
        p.setFallbackModels(List.of("gemini-2.0-flash", "gemini-2.5-flash", "  gemini-2.0-flash  "));
        assertEquals(List.of("gemini-2.5-flash", "gemini-2.0-flash"), p.getModelAttemptOrder());
    }

    @Test
    void getModelAttemptOrder_onlyFallbacksWhenPrimaryBlank() {
        GeminiProperties p = new GeminiProperties();
        p.setModel(" ");
        p.setFallbackModels(List.of("gemini-2.0-flash"));
        assertEquals(List.of("gemini-2.0-flash"), p.getModelAttemptOrder());
    }
}
