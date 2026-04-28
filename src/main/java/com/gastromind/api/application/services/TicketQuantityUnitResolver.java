package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.ports.out.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
/**
 * Traduce unidades detectadas por IA a unidades canónicas del sistema.
 */
public class TicketQuantityUnitResolver {

    private final UnitRepository unitRepository;
    /**
     * Crea el servicio con acceso al repositorio de unidades.
     * @param unitRepository repositorio de unidades de medida
     */

    public TicketQuantityUnitResolver(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }
    /**
     * Resuelve un código de unidad devuelto por IA a una unidad persistida.
     * @param rawCode código de unidad recibido (por ejemplo, g, kg, ud)
     * @return unidad de medida equivalente en base de datos
     * @throws IllegalStateException si no existe la unidad semilla {@code Unidades}
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
     * Convierte una unidad de base de datos a su código canónico corto.
     * @param unit unidad de medida
     * @return código canónico ({@code g}, {@code kg}, {@code ml}, {@code l} o {@code ud})
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
     * Convierte el nombre de una unidad de base de datos a código canónico.
     * @param unitName nombre de unidad almacenado en base de datos
     * @return código canónico ({@code g}, {@code kg}, {@code ml}, {@code l} o {@code ud})
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




