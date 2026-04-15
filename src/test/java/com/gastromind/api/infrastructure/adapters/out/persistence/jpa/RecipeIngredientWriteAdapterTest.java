package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.RecipeIngredientUsage;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.RecipeIngredientEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UnitEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.ProductJpaRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.RecipeIngredientJpaRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UnitJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeIngredientWriteAdapterTest {

    @Mock
    private RecipeIngredientJpaRepository recipeIngredientJpaRepository;
    @Mock
    private UnitJpaRepository unitJpaRepository;
    @Mock
    private ProductJpaRepository productJpaRepository;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private RecipeIngredientWriteAdapter adapter;

    private void wireEntityManager() {
        ReflectionTestUtils.setField(adapter, "entityManager", entityManager);
    }

    private UnitEntity unit() {
        UnitEntity unit = new UnitEntity();
        unit.setId("u-1");
        return unit;
    }

    private void stubRecipeAndProductRefs() {
        RecipeEntity recipeRef = new RecipeEntity();
        ProductEntity productRef = new ProductEntity();
        when(entityManager.getReference(RecipeEntity.class, "r-1")).thenReturn(recipeRef);
        when(entityManager.getReference(ProductEntity.class, "p-1")).thenReturn(productRef);
    }

    @Test
    void saveForRecipe_noOpWhenNullOrEmpty() {
        adapter.saveForRecipe("r-1", null);
        adapter.saveForRecipe("r-1", List.of());
        verify(recipeIngredientJpaRepository, never()).save(any());
    }

    @Test
    void saveForRecipe_skipsNullEmptyStringAndWhitespaceProductId() {
        wireEntityManager();
        UnitEntity unit = unit();
        when(unitJpaRepository.findByName("Unidades")).thenReturn(Optional.of(unit));
        when(entityManager.getReference(RecipeEntity.class, "r-1")).thenReturn(new RecipeEntity());
        when(productJpaRepository.existsById("p-1")).thenReturn(true);
        when(entityManager.getReference(ProductEntity.class, "p-1")).thenReturn(new ProductEntity());

        RecipeIngredientUsage nulled = new RecipeIngredientUsage();
        nulled.setProductId(null);
        RecipeIngredientUsage empty = new RecipeIngredientUsage();
        empty.setProductId("");
        RecipeIngredientUsage ok = new RecipeIngredientUsage();
        ok.setProductId("p-1");
        ok.setQuantityUsed(BigDecimal.ONE);

        adapter.saveForRecipe("r-1", List.of(nulled, empty, ok));
        verify(recipeIngredientJpaRepository).save(any());
    }

    @Test
    void saveForRecipe_skipsBlankProductAndMissingCatalog() {
        wireEntityManager();
        UnitEntity unit = unit();
        when(unitJpaRepository.findByName("Unidades")).thenReturn(Optional.of(unit));
        when(entityManager.getReference(RecipeEntity.class, "r-1")).thenReturn(new RecipeEntity());
        when(productJpaRepository.existsById("p-1")).thenReturn(false);

        RecipeIngredientUsage ok = new RecipeIngredientUsage();
        ok.setProductId("p-1");
        ok.setQuantityUsed(BigDecimal.ONE);

        RecipeIngredientUsage blank = new RecipeIngredientUsage();
        blank.setProductId(" ");
        RecipeIngredientUsage missing = new RecipeIngredientUsage();
        missing.setProductId("p-1");

        adapter.saveForRecipe("r-1", List.of(blank, missing, ok));
        verify(recipeIngredientJpaRepository, never()).save(any());
    }

    @Test
    void saveForRecipe_persistsRows() {
        wireEntityManager();
        UnitEntity unit = unit();
        RecipeEntity recipeRef = new RecipeEntity();
        ProductEntity productRef = new ProductEntity();
        when(entityManager.getReference(RecipeEntity.class, "r-1")).thenReturn(recipeRef);
        when(entityManager.getReference(ProductEntity.class, "p-1")).thenReturn(productRef);
        when(unitJpaRepository.findByName("Unidades")).thenReturn(Optional.of(unit));
        when(productJpaRepository.existsById("p-1")).thenReturn(true);

        RecipeIngredientUsage u = new RecipeIngredientUsage();
        u.setProductId("p-1");
        u.setQuantityUsed(new BigDecimal("3"));

        adapter.saveForRecipe("r-1", List.of(u));

        ArgumentCaptor<RecipeIngredientEntity> cap = ArgumentCaptor.forClass(RecipeIngredientEntity.class);
        verify(recipeIngredientJpaRepository).save(cap.capture());
        assertSame(recipeRef, cap.getValue().getRecipe());
        assertSame(productRef, cap.getValue().getProduct());
        assertSame(unit, cap.getValue().getUnit());
    }

    @Test
    void saveForRecipe_resolvesUnitFromFallbackChain() {
        wireEntityManager();
        UnitEntity unit = unit();
        stubRecipeAndProductRefs();
        when(unitJpaRepository.findByName("Unidades")).thenReturn(Optional.empty());
        when(unitJpaRepository.findFirstByNameOrderByIdAsc("Unidades")).thenReturn(Optional.empty());
        when(unitJpaRepository.findAll()).thenReturn(List.of(unit));
        when(productJpaRepository.existsById("p-1")).thenReturn(true);

        RecipeIngredientUsage u = new RecipeIngredientUsage();
        u.setProductId("p-1");
        u.setQuantityUsed(null);

        adapter.saveForRecipe("r-1", List.of(u));
        verify(recipeIngredientJpaRepository).save(any());
    }

    @Test
    void saveForRecipe_throwsWhenNoUnitsInCatalog() {
        when(unitJpaRepository.findByName("Unidades")).thenReturn(Optional.empty());
        when(unitJpaRepository.findFirstByNameOrderByIdAsc("Unidades")).thenReturn(Optional.empty());
        when(unitJpaRepository.findAll()).thenReturn(List.of());

        RecipeIngredientUsage u = new RecipeIngredientUsage();
        u.setProductId("p-1");
        assertThrows(IllegalStateException.class, () -> adapter.saveForRecipe("r-1", List.of(u)));
    }
}
