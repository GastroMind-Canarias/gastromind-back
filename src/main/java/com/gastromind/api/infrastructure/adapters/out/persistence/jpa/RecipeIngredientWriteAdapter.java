package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.RecipeIngredientUsage;
import com.gastromind.api.domain.ports.out.RecipeIngredientWritePort;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeIngredientEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UnitEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.ProductJpaRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.RecipeIngredientJpaRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UnitJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class RecipeIngredientWriteAdapter implements RecipeIngredientWritePort {

    private static final String DEFAULT_UNIT_NAME = "Unidades";

    private final RecipeIngredientJpaRepository recipeIngredientJpaRepository;
    private final UnitJpaRepository unitJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public RecipeIngredientWriteAdapter(
            RecipeIngredientJpaRepository recipeIngredientJpaRepository,
            UnitJpaRepository unitJpaRepository,
            ProductJpaRepository productJpaRepository) {
        this.recipeIngredientJpaRepository = recipeIngredientJpaRepository;
        this.unitJpaRepository = unitJpaRepository;
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public void saveForRecipe(String recipeId, List<RecipeIngredientUsage> usages) {
        if (usages == null || usages.isEmpty()) {
            return;
        }
        UnitEntity unit = unitJpaRepository.findByName(DEFAULT_UNIT_NAME)
                .or(() -> unitJpaRepository.findFirstByNameOrderByIdAsc(DEFAULT_UNIT_NAME))
                .orElseGet(() -> unitJpaRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new IllegalStateException("No hay unidades en catálogo")));
        RecipeEntity recipeRef = entityManager.getReference(RecipeEntity.class, recipeId);
        for (RecipeIngredientUsage u : usages) {
            if (u.getProductId() == null || u.getProductId().isBlank()) {
                continue;
            }
            if (!productJpaRepository.existsById(u.getProductId())) {
                continue;
            }
            RecipeIngredientEntity row = new RecipeIngredientEntity();
            row.setRecipe(recipeRef);
            row.setProduct(entityManager.getReference(ProductEntity.class, u.getProductId()));
            BigDecimal q = u.getQuantityUsed() != null ? u.getQuantityUsed() : BigDecimal.ZERO;
            row.setQuantityRequired(q);
            row.setUnit(unit);
            recipeIngredientJpaRepository.save(row);
        }
    }
}
