package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
/**
 * Resuelve productos de catálogo a partir de nombres detectados en tickets.
 */
public class TicketProductResolutionService {

    private static final Pattern SPACES = Pattern.compile("\\s+");

    private final ProductRepository productRepository;
    /**
     * Crea el servicio con acceso al repositorio de productos.
     * @param productRepository repositorio de productos de catálogo
     */

    public TicketProductResolutionService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    /**
     * Busca un producto del catálogo a partir del nombre de una línea de ticket.
     * @param rawName nombre detectado en la línea del ticket
     * @return producto encontrado, si existe coincidencia
     * @throws IllegalArgumentException si el nombre queda vacío tras normalizarlo
     */

    public Optional<Product> findCatalogProductByName(String rawName) {
        String name = normalizeName(rawName);
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Nombre de producto vacio en una linea del ticket");
        }
        return productRepository.findFirstByNameIgnoreCase(name);
    }
    /**
     * Normaliza un nombre de producto eliminando espacios redundantes.
     * @param raw texto original
     * @return nombre normalizado o cadena vacía si la entrada es nula
     */

    public static String normalizeName(String raw) {
        if (raw == null) {
            return "";
        }
        return SPACES.matcher(raw.trim()).replaceAll(" ");
    }
}




