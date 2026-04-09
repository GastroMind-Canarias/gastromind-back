package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Política de productos para tickets: emparejar por nombre (sin distinguir mayúsculas) y,
 * si no existe, crear un producto nuevo en catálogo con ese nombre.
 */
@Service
public class TicketProductResolutionService {

    private static final Pattern SPACES = Pattern.compile("\\s+");

    private final ProductRepository productRepository;

    public TicketProductResolutionService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product resolveOrCreate(String rawName) {
        String name = normalizeName(rawName);
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Nombre de producto vacío en una línea del ticket");
        }
        return productRepository.findFirstByNameIgnoreCase(name).orElseGet(() -> createProduct(name));
    }

    private Product createProduct(String name) {
        Product p = new Product();
        p.setName(name);
        p.setIs_essential(false);
        p.setNeedsReview(true);
        p.setReviewNote("Creado automáticamente desde un ticket. Revisa nombre y categoría.");
        return productRepository.save(p);
    }

    public static String normalizeName(String raw) {
        if (raw == null) {
            return "";
        }
        return SPACES.matcher(raw.trim()).replaceAll(" ");
    }
}
