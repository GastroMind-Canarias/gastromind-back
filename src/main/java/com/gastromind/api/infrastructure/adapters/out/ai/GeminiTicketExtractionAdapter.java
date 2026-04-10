package com.gastromind.api.infrastructure.adapters.out.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gastromind.api.domain.exceptions.AiTicketException;
import com.gastromind.api.domain.models.ticket.ExtractedTicketLine;
import com.gastromind.api.domain.models.ticket.ExtractedTicketReceipt;
import com.gastromind.api.domain.ports.out.TicketExtractionPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class GeminiTicketExtractionAdapter implements TicketExtractionPort {

    private static final Set<String> ALLOWED_UNITS = Set.of("g", "kg", "ml", "l", "ud");

    private final GeminiProperties properties;
    private final GeminiGenerateContentClient geminiClient;
    private final ObjectMapper objectMapper;

    public GeminiTicketExtractionAdapter(
            GeminiProperties properties,
            GeminiGenerateContentClient geminiClient,
            ObjectMapper objectMapper) {
        this.properties = properties;
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public ExtractedTicketReceipt extractFromImage(byte[] imageBytes, String mimeType) {
        if (!properties.isConfigured()) {
            throw new AiTicketException("Extracción de tickets por IA no configurada (falta app.ai.gemini.api-key)");
        }
        if (imageBytes == null || imageBytes.length == 0) {
            throw new AiTicketException("La imagen del ticket está vacía");
        }

        String prompt = buildPrompt();
        String requestBody = buildMultimodalRequestBody(prompt, imageBytes, mimeType);

        try {
            String raw = geminiClient.postGenerateContent(requestBody);
            return parseReceiptResponse(raw);
        } catch (RestClientException e) {
            throw new AiTicketException("Error al llamar a Gemini: " + e.getMessage(), e);
        } catch (AiTicketException e) {
            throw e;
        } catch (Exception e) {
            throw new AiTicketException("No se pudo interpretar la respuesta de IA del ticket", e);
        }
    }

    private String buildPrompt() {
        return """
                Eres un extractor especializado en tickets de SUPERMERCADO para una app de cocina y despensa.
                Analiza la imagen y responde SOLO con un JSON válido (sin markdown ni texto fuera del JSON) con exactamente esta forma:
                {
                  "store_name": string o null,
                  "purchase_date": string o null (YYYY-MM-DD),
                  "total_amount": number (total a pagar del ticket completo, si se lee),
                  "lines": [
                    {
                      "product_name": string,
                      "quantity_amount": number,
                      "quantity_unit": "g" | "kg" | "ml" | "l" | "ud",
                      "unit_price": number o null,
                      "line_total": number o null,
                      "line_needs_verification": boolean,
                      "line_quality_note": string o null
                    }
                  ]
                }

                INCLUSIÓN DE LÍNEAS (solo alimentación y hogar relacionado con cocina):
                - Incluye: alimentos, bebidas comestibles, frescos, ultramarinos, congelados, panadería, lácteos, aceites COMESTIBLES, etc.
                - NO incluyas: motor/coche (aceites de motor, limpiacristales coche, parachoques…), ferretería no alimentaria, bolsas de plástico de compra solas, pilas, revistas, medicamentos si no es relevante, productos de limpieza no alimentarios salvo que sean claramente para cocina (p. ej. lavavajillas podría excluirse si la app es solo comida — en caso de duda, excluye lo que no sea comestible o ingrediente de cocina).
                - Si una línea no es claramente producto de alimentación/cocina, no la incluyas.

                NOMBRES:
                - Escribe product_name en español correcto, SIN abreviaturas de ticket: expande (ej. "S/GRA" → "sin grasa", "ATÚN" puede quedarse, "PAVO" → "pavo").
                - Capitalización natural (no todo en mayúsculas salvo marcas conocidas).

                CANTIDAD Y UNIDAD:
                - quantity_amount y quantity_unit deben reflejar lo COMPRADO, no el precio por peso:
                  - Si el ticket dice 450 g de pechuga, quantity_amount=450 y quantity_unit="g" (no uses 1 ud para peso vendido a granel/precio/kg).
                  - Si son 2 bricks de leche de 1 L, quantity_amount=2 y quantity_unit="ud" (o si el ticket muestra 2 L total, puedes usar quantity_amount=2 y quantity_unit="l" si es más fiel al ticket).
                  - Para packs "x6" zumos: si es un pack de 6 unidades, quantity_amount=1 y quantity_unit="ud" si se cobra el pack entero, o refleja botellas si el ticket separa líneas.
                - quantity_unit solo puede ser: g, kg, ml, l, ud.

                PRECIOS:
                - unit_price: precio unitario que permita entender la línea (típicamente €/kg si venden a peso, €/ud si es unidad). Si no es legible, null.
                - line_total: importe de la línea si aparece.

                REVISIÓN DE LÍNEA (para la app):
                - line_needs_verification: true si hay dudas reales: peso/volumen no legible o ambiguo, cantidad incierta, precio ilegible, o solo ves precio/unidad pero no cuánto se compró en gramos/ml.
                - line_quality_note: en español, breve (ej. "No se distingue el peso en gramos; se asume 1 ud."). Si no hay incidencias, null y line_needs_verification false.

                OTROS:
                - Agrupa líneas duplicadas del mismo producto sumando quantity_amount (misma unidad).
                - Omite cabeceras, totales duplicados y líneas de IVA sin detalle.
                - total_amount: total del ticket si se ve; si no, 0.
                """;
    }

    private String buildMultimodalRequestBody(String prompt, byte[] imageBytes, String mimeType) {
        try {
            String b64 = Base64.getEncoder().encodeToString(imageBytes);
            var root = objectMapper.createObjectNode();
            var contents = root.putArray("contents");
            var content = contents.addObject();
            var parts = content.putArray("parts");
            parts.addObject().put("text", prompt);
            ObjectNode inline = parts.addObject().putObject("inline_data");
            inline.put("mime_type", mimeType != null ? mimeType : "image/jpeg");
            inline.put("data", b64);
            var gen = root.putObject("generationConfig");
            gen.put("temperature", 0.1);
            gen.put("responseMimeType", "application/json");
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new AiTicketException("Error construyendo petición multimodal a Gemini", e);
        }
    }

    private ExtractedTicketReceipt parseReceiptResponse(String rawJson) throws Exception {
        JsonNode root = objectMapper.readTree(rawJson);
        JsonNode candidates = root.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            throw new AiTicketException("Respuesta de Gemini sin candidatos");
        }
        String text = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
        if (text.isBlank()) {
            throw new AiTicketException("Respuesta de Gemini vacía");
        }

        JsonNode receipt = objectMapper.readTree(text);
        String storeName = receipt.path("store_name").asText(null);
        if (storeName != null && storeName.isBlank()) {
            storeName = null;
        }

        LocalDate purchaseDate = null;
        String dateStr = receipt.path("purchase_date").asText(null);
        if (dateStr != null && !dateStr.isBlank()) {
            try {
                purchaseDate = LocalDate.parse(dateStr.trim());
            } catch (DateTimeParseException ignored) {
                purchaseDate = null;
            }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (receipt.path("total_amount").isNumber()) {
            totalAmount = receipt.get("total_amount").decimalValue();
        }

        List<ExtractedTicketLine> lines = new ArrayList<>();
        JsonNode linesNode = receipt.path("lines");
        if (linesNode.isArray()) {
            for (JsonNode n : linesNode) {
                String productName = n.path("product_name").asText("").trim();
                if (productName.isEmpty()) {
                    continue;
                }

                BigDecimal qtyAmt = BigDecimal.ONE;
                if (n.path("quantity_amount").isNumber()) {
                    qtyAmt = n.get("quantity_amount").decimalValue();
                } else if (n.path("quantity").isNumber()) {
                    qtyAmt = n.get("quantity").decimalValue();
                }
                if (qtyAmt.compareTo(BigDecimal.ZERO) <= 0) {
                    qtyAmt = BigDecimal.ONE;
                }

                String qUnit = normalizeQuantityUnit(n.path("quantity_unit").asText("ud"));

                BigDecimal unitPrice = BigDecimal.ZERO;
                if (n.path("unit_price").isNumber()) {
                    unitPrice = n.get("unit_price").decimalValue();
                }
                if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                    unitPrice = BigDecimal.ZERO;
                }

                BigDecimal lineTotal = null;
                if (n.path("line_total").isNumber()) {
                    lineTotal = n.get("line_total").decimalValue();
                }

                boolean lineNeedsVerification = n.path("line_needs_verification").asBoolean(false);
                String lineQualityNote = n.path("line_quality_note").asText(null);
                if (lineQualityNote != null && lineQualityNote.isBlank()) {
                    lineQualityNote = null;
                }

                lines.add(new ExtractedTicketLine(
                        productName, qtyAmt, qUnit, unitPrice, lineTotal, lineNeedsVerification, lineQualityNote));
            }
        }

        if (lines.isEmpty()) {
            throw new AiTicketException("No se detectaron líneas de producto de alimentación en el ticket");
        }

        return new ExtractedTicketReceipt(storeName, purchaseDate, totalAmount, lines);
    }

    private String normalizeQuantityUnit(String raw) {
        if (raw == null || raw.isBlank()) {
            return "ud";
        }
        String u = raw.trim().toLowerCase(Locale.ROOT);
        if ("l.".equals(u) || "ltr".equals(u) || "litro".equals(u) || "litros".equals(u)) {
            u = "l";
        }
        if ("gr".equals(u) || "grs".equals(u) || "gramos".equals(u)) {
            u = "g";
        }
        if ("kilos".equals(u) || "kilogramo".equals(u) || "kilogramos".equals(u)) {
            u = "kg";
        }
        if ("mililitros".equals(u) || "cc".equals(u)) {
            u = "ml";
        }
        if ("unidad".equals(u) || "unidades".equals(u) || "uds".equals(u) || "u".equals(u)) {
            u = "ud";
        }
        if (!ALLOWED_UNITS.contains(u)) {
            return "ud";
        }
        return u;
    }
}
