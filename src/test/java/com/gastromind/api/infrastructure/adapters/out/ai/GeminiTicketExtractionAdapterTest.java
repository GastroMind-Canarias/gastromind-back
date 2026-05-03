package com.gastromind.api.infrastructure.adapters.out.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gastromind.api.domain.exceptions.AiTicketException;
import com.gastromind.api.domain.models.ticket.ExtractedTicketReceipt;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeminiTicketExtractionAdapterTest {

    @Test
    void extractFromImage_shouldFailWhenAiNotConfiguredOrImageEmpty() {
        GeminiProperties props = new GeminiProperties();
        props.setApiKey(" ");
        GeminiTicketExtractionAdapter adapter = new GeminiTicketExtractionAdapter(
                props, mock(GeminiGenerateContentClient.class), new ObjectMapper());

        AiTicketException ex1 = assertThrows(AiTicketException.class,
                () -> adapter.extractFromImage(new byte[]{1}, "image/png"));
        assertEquals("Extraccion de tickets por IA no configurada (falta app.ai.gemini.api-key)", ex1.getMessage());

        props.setApiKey("k");
        AiTicketException ex2 = assertThrows(AiTicketException.class,
                () -> adapter.extractFromImage(new byte[]{}, "image/png"));
        assertEquals("La imagen del ticket esta vacia", ex2.getMessage());
    }

    @Test
    void extractFromImage_shouldParseAndNormalizeLines() {
        GeminiProperties props = new GeminiProperties();
        props.setApiKey("k");
        GeminiGenerateContentClient client = mock(GeminiGenerateContentClient.class);
        GeminiTicketExtractionAdapter adapter = new GeminiTicketExtractionAdapter(props, client, new ObjectMapper());

        String raw = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "{\\"store_name\\":\\" \\\",\\"purchase_date\\":\\"bad-date\\",\\"total_amount\\":10.5,\\"lines\\":[{\\"product_name\\":\\"Tomate\\",\\"quantity_amount\\":-2,\\"quantity_unit\\":\\"grs\\",\\"unit_price\\":-1,\\"line_total\\":2.0,\\"line_needs_verification\\":true,\\"line_quality_note\\":\\"  dudoso \\"},{\\"product_name\\":\\"\\",\\"quantity_amount\\":1,\\"quantity_unit\\":\\"ud\\"}]}"
                          }
                        ]
                      }
                    }
                  ]
                }
                """;
        when(client.postGenerateContent(anyString())).thenReturn(raw);

        ExtractedTicketReceipt out = adapter.extractFromImage(new byte[]{1, 2}, "image/png");
        assertNull(out.storeName());
        assertNull(out.purchaseDate());
        assertEquals(1, out.lines().size());
        assertEquals("Tomate", out.lines().getFirst().productName());
        assertEquals("g", out.lines().getFirst().quantityUnit());
        assertEquals("  dudoso ", out.lines().getFirst().lineQualityNote());
        assertEquals(0, out.lines().getFirst().unitPrice().signum());
        assertEquals(1, out.lines().getFirst().quantityAmount().intValue());
    }

    @Test
    void extractFromImage_shouldFailWhenNoFoodLines() {
        GeminiProperties props = new GeminiProperties();
        props.setApiKey("k");
        GeminiGenerateContentClient client = mock(GeminiGenerateContentClient.class);
        GeminiTicketExtractionAdapter adapter = new GeminiTicketExtractionAdapter(props, client, new ObjectMapper());
        when(client.postGenerateContent(anyString())).thenReturn("""
                {"candidates":[{"content":{"parts":[{"text":"{\\"store_name\\":\\"A\\",\\"total_amount\\":0,\\"lines\\":[]}" } ]}}]}
                """);

        AiTicketException ex = assertThrows(AiTicketException.class,
                () -> adapter.extractFromImage(new byte[]{1}, "image/png"));
        assertEquals("No se detectaron lineas de producto de alimentacion en el ticket", ex.getMessage());
    }

    @Test
    void extractFromImage_shouldWrapRestErrors() {
        GeminiProperties props = new GeminiProperties();
        props.setApiKey("k");
        GeminiGenerateContentClient client = mock(GeminiGenerateContentClient.class);
        GeminiTicketExtractionAdapter adapter = new GeminiTicketExtractionAdapter(props, client, new ObjectMapper());
        when(client.postGenerateContent(anyString())).thenThrow(new RestClientException("timeout"));

        AiTicketException ex = assertThrows(AiTicketException.class,
                () -> adapter.extractFromImage(new byte[]{1}, "image/png"));
        assertEquals("Error al llamar a Gemini: timeout", ex.getMessage());
    }
}
