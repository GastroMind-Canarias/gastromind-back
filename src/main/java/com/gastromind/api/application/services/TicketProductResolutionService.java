package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Política de productos para tickets: emparejar por nombre (sin distinguir mayúsculas).
 * No crea filas en catálogo; líneas sin coincidencia usan solo texto en ticket/fridge.
 */
@Service
public class TicketProductResolutionService {

    private static final Pattern SPACES = Pattern.compile("\\s+");

    private final ProductRepository productRepository;

    public TicketProductResolutionService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> findCatalogProductByName(String rawName) {
        String name = normalizeName(rawName);
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Nombre de producto vacío en una línea del ticket");
        }
        return productRepository.findFirstByNameIgnoreCase(name);
    }

    public static String normalizeName(String raw) {
        if (raw == null) {
            return "";
        }
        return SPACES.matcher(raw.trim()).replaceAll(" ");
    }
}
