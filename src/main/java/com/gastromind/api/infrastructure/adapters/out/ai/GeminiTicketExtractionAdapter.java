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
/**
 * Representa gemini ticket extraction dentro del dominio de la aplicacion.
 */
public class GeminiTicketExtractionAdapter implements TicketExtractionPort {

    private static final Set<String> ALLOWED_UNITS = Set.of("g", "kg", "ml", "l", "ud");

    private final GeminiProperties properties;
    private final GeminiGenerateContentClient geminiClient;
    private final ObjectMapper objectMapper;
    /**
     * Constructor de gemini ticket extraction.
     * @param properties valor a utilizar.
     * @param geminiClient valor a utilizar.
     * @param objectMapper valor a utilizar.
     */

    public GeminiTicketExtractionAdapter(
            GeminiProperties properties,
            GeminiGenerateContentClient geminiClient,
            ObjectMapper objectMapper) {
        this.properties = properties;
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }
    /**
     * Realiza extract from image.
     * @param imageBytes valor a utilizar.
     * @param mimeType valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Override
    public ExtractedTicketReceipt extractFromImage(byte[] imageBytes, String mimeType) {
        if (!properties.isConfigured()) {
            throw new AiTicketException("Extraccion de tickets por IA no configurada (falta app.ai.gemini.api-key)");
        }
        if (imageBytes == null || imageBytes.length == 0) {
            throw new AiTicketException("La imagen del ticket esta vacia");
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
                Analiza la imagen y responde SOLO con un JSON vAAaAaAaaAAaAAasAAlido (sin markdown ni texto fuera del JSON) con exactamente esta forma:
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

                INCLUSIAAaAaAaaAAAAAAaAAAAaAaAN DE LAAaAaAaaAAaAAasAANEAS (solo alimentaciAAaAaAaaAAaAAasAAn y hogar relacionado con cocina):
                - Incluye: alimentos, bebidas comestibles, frescos, ultramarinos, congelados, panaderAAaAaAaaAAaAAasAAa, lAAaAaAaaAAaAAasAActeos, aceites COMESTIBLES, etc.
                - NO incluyas: motor/coche (aceites de motor, limpiacristales coche, parachoquesAAaAasAAAAAAAAasAAAAasAAAAaAAasAA), ferreterAAaAaAaaAAaAAasAAa no alimentaria, bolsas de plAAaAaAaaAAaAAasAAstico de compra solas, pilas, revistas, medicamentos si no es relevante, productos de limpieza no alimentarios salvo que sean claramente para cocina (p. ej. lavavajillas podrAAaAaAaaAAaAAasAAa excluirse si la app es solo comida AAaAasAAAAAAAAasAAAAasAAAAAAAAaAAAAasAA en caso de duda, excluye lo que no sea comestible o ingrediente de cocina).
                - Si una lAAaAaAaaAAaAAasAAnea no es claramente producto de alimentaciAAaAaAaaAAaAAasAAn/cocina, no la incluyas.

                NOMBRES:
                - Escribe product_name en espaAAaAaAaaAAaAAasAAol correcto, SIN abreviaturas de ticket: expande (ej. "S/GRA" AAaAasAAAAAAAAaAAAAasAAAAAAAAaAAAAAAaAAA "sin grasa", "ATAAaAaAaaAAaAAasAAN" puede quedarse, "PAVO" AAaAasAAAAAAAAaAAAAasAAAAAAAAaAAAAAAaAAA "pavo").
                - CapitalizaciAAaAaAaaAAaAAasAAn natural (no todo en mayAAaAaAaaAAaAAasAAsculas salvo marcas conocidas).

                CANTIDAD Y UNIDAD:
                - quantity_amount y quantity_unit deben reflejar lo COMPRADO, no el precio por peso:
                  - Si el ticket dice 450 g de pechuga, quantity_amount=450 y quantity_unit="g" (no uses 1 ud para peso vendido a granel/precio/kg).
                  - Si son 2 bricks de leche de 1 L, quantity_amount=2 y quantity_unit="ud" (o si el ticket muestra 2 L total, puedes usar quantity_amount=2 y quantity_unit="l" si es mAAaAaAaaAAaAAasAAs fiel al ticket).
                  - Para packs "x6" zumos: si es un pack de 6 unidades, quantity_amount=1 y quantity_unit="ud" si se cobra el pack entero, o refleja botellas si el ticket separa lAAaAaAaaAAaAAasAAneas.
                - quantity_unit solo puede ser: g, kg, ml, l, ud.

                PRECIOS:
                - unit_price: precio unitario que permita entender la lAAaAaAaaAAaAAasAAnea (tAAaAaAaaAAaAAasAApicamente AAaAasAAAAAAAAaAAAAaAAAAaAAasAA/kg si venden a peso, AAaAasAAAAAAAAaAAAAaAAAAaAAasAA/ud si es unidad). Si no es legible, null.
                - line_total: importe de la lAAaAaAaaAAaAAasAAnea si aparece.

                REVISIAAaAaAaaAAAAAAaAAAAaAaAN DE LAAaAaAaaAAaAAasAANEA (para la app):
                - line_needs_verification: true si hay dudas reales: peso/volumen no legible o ambiguo, cantidad incierta, precio ilegible, o solo ves precio/unidad pero no cuAAaAaAaaAAaAAasAAnto se comprAAaAaAaaAAaAAasAA en gramos/ml.
                - line_quality_note: en espaAAaAaAaaAAaAAasAAol, breve (ej. "No se distingue el peso en gramos; se asume 1 ud."). Si no hay incidencias, null y line_needs_verification false.

                OTROS:
                - Agrupa lAAaAaAaaAAaAAasAAneas duplicadas del mismo producto sumando quantity_amount (misma unidad).
                - Omite cabeceras, totales duplicados y lAAaAaAaaAAaAAasAAneas de IVA sin detalle.
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
            throw new AiTicketException("Error construyendo peticiAAaAaAaaAAaAAasAAn multimodal a Gemini", e);
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
            throw new AiTicketException("Respuesta de Gemini vacia");
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
            throw new AiTicketException("No se detectaron lineas de producto de alimentacion en el ticket");
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




