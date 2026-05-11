package com.gastromind.api.domain.ports.out;

import com.gastromind.api.domain.models.ProductAlias;

import java.util.Optional;

/**
 * Sinónimos de producto resueltos contra el catálogo para reconciliar líneas de ticket.
 */
public interface ProductAliasRepository {
    ProductAlias save(ProductAlias alias);

    Optional<ProductAlias> findFirstByAliasNorm(String aliasNorm);
}
