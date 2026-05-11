package com.gastromind.api.application.services;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.ProductAlias;
import com.gastromind.api.domain.ports.out.ProductAliasRepository;
import com.gastromind.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
/**
 * Resuelve productos de catalogo a partir de nombres detectados en tickets.
 */
public class TicketProductResolutionService {

    private static final Pattern SPACES = Pattern.compile("\\s+");

    private final ProductRepository productRepository;
    private final ProductAliasRepository productAliasRepository;
    /**
     * Crea el servicio con acceso al repositorio de productos.
     * @param productRepository repositorio de productos de catalogo
     */

    public TicketProductResolutionService(
            ProductRepository productRepository,
            ProductAliasRepository productAliasRepository) {
        this.productRepository = productRepository;
        this.productAliasRepository = productAliasRepository;
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

    public Product resolveOrCreateProduct(String rawName) {
        return resolveOrCreateProduct(rawName, true, "Creado automaticamente por OCR pendiente de revision");
    }

    public Product resolveOrCreateProductFromManualEntry(String rawName) {
        return resolveOrCreateProduct(rawName, false, null);
    }

    private Product resolveOrCreateProduct(String rawName, boolean needsReviewOnCreate, String reviewNoteOnCreate) {
        String normalized = normalizeName(rawName);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Nombre de producto vacio en una linea del ticket");
        }

        Optional<Product> exact = productRepository.findFirstByNameIgnoreCase(normalized);
        if (exact.isPresent()) {
            ensureAlias(normalized, rawName, exact.get());
            return exact.get();
        }

        Optional<ProductAlias> alias = productAliasRepository.findFirstByAliasNorm(normalized);
        if (alias.isPresent()) {
            Product product = productRepository.findById(alias.get().getProductId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Alias de producto apunta a un producto inexistente: " + alias.get().getProductId()));
            ensureAlias(normalized, rawName, product);
            return product;
        }

        Product provisional = new Product();
        provisional.setName(normalized);
        provisional.setNeedsReview(needsReviewOnCreate);
        provisional.setReviewNote(reviewNoteOnCreate);
        provisional.setIs_essential(false);
        Product saved = productRepository.save(provisional);
        ensureAlias(normalized, rawName, saved);
        return saved;
    }

    private void ensureAlias(String normalizedName, String rawName, Product product) {
        if (product.getId() == null) {
            return;
        }
        if (productAliasRepository.findFirstByAliasNorm(normalizedName).isPresent()) {
            return;
        }
        ProductAlias alias = new ProductAlias();
        alias.setProductId(product.getId());
        alias.setAlias(normalizeName(rawName));
        alias.setAliasNorm(normalizedName);
        productAliasRepository.save(alias);
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
        return SPACES.matcher(raw.trim()).replaceAll(" ").toLowerCase(Locale.ROOT);
    }
}




