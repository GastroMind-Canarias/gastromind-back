package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.ports.out.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Enlaza códigos de unidad devueltos por la IA (g, kg, ml, l, ud) con filas de {@code unit} en BD.
 */
@Service
public class TicketQuantityUnitResolver {

    private final UnitRepository unitRepository;

    public TicketQuantityUnitResolver(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    /**
     * Resuelve un código corto de ticket al {@link Unit} persistido (nombre en español en BD).
     */
    public Unit resolveFromAiCode(String rawCode) {
        String c = rawCode == null ? "ud" : rawCode.trim().toLowerCase(Locale.ROOT);
        if (c.isEmpty()) {
            c = "ud";
        }
        String dbName = switch (c) {
            case "g", "gr", "grs" -> "Gramos";
            case "kg", "kilo", "kilos" -> "Kilogramos";
            case "ml", "cc" -> "Mililitros";
            case "l", "ltr", "litro", "litros" -> "Litros";
            case "ud", "uds", "u", "unidad", "unidades" -> "Unidades";
            default -> "Unidades";
        };
        return unitRepository.findFirstByNameIgnoreCase(dbName)
                .orElseGet(() -> unitRepository.findFirstByNameIgnoreCase("Unidades")
                        .orElseThrow(() -> new IllegalStateException(
                                "No hay unidad semilla 'Unidades' en la tabla unit. Ejecute data.sql o cree unidades.")));
    }

    /**
     * Código estable para cálculos (precio, importe de línea).
     */
    public static String canonicalCode(Unit unit) {
        if (unit == null || unit.getName() == null) {
            return "ud";
        }
        String n = unit.getName().trim().toLowerCase(Locale.ROOT);
        if (n.equals("gramos")) {
            return "g";
        }
        if (n.equals("kilogramos")) {
            return "kg";
        }
        if (n.equals("mililitros")) {
            return "ml";
        }
        if (n.equals("litros")) {
            return "l";
        }
        if (n.equals("unidades")) {
            return "ud";
        }
        return "ud";
    }

    /**
     * Igual que {@link #canonicalCode(Unit)} pero con el nombre tal como viene de BD (join en ticket_items).
     */
    public static String canonicalCodeFromDbUnitName(String unitName) {
        if (unitName == null || unitName.isBlank()) {
            return "ud";
        }
        String n = unitName.trim().toLowerCase(Locale.ROOT);
        return switch (n) {
            case "gramos" -> "g";
            case "kilogramos" -> "kg";
            case "mililitros" -> "ml";
            case "litros" -> "l";
            case "unidades" -> "ud";
            default -> "ud";
        };
    }
}
