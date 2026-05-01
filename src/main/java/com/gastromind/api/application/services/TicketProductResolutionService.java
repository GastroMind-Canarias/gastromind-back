package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
/**
 * Resuelve productos de catalogo a partir de nombres detectados en tickets.
 */
public class TicketProductResolutionService {

    private static final Pattern SPACES = Pattern.compile("\\s+");

    private final ProductRepository productRepository;
    /**
     * Crea el servicio con acceso al repositorio de productos.
     * @param productRepository repositorio de productos de catalogo
     */

    public TicketProductResolutionService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    /**
     * Busca un producto del catalogo a partir del nombre de una lAnea de ticket.
     * @param rawName nombre detectado en la lAnea del ticket
     * @return producto encontrado, si existe coincidencia
     * @throws IllegalArgumentException si el nombre queda vacio tras normalizarlo
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
     * @return nombre normalizado o cadena vacAa si la entrada es nula
     */

    public static String normalizeName(String raw) {
        if (raw == null) {
            return "";
        }
        return SPACES.matcher(raw.trim()).replaceAll(" ");
    }
}




