package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.CategoryServiceImpl;
import com.gastromind.api.application.services.ProductServiceImpl;
import com.gastromind.api.application.services.RecipeServiceImpl;
import com.gastromind.api.application.services.StoreServiceImpl;
import com.gastromind.api.application.services.UnitServiceImpl;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.application.usecases.GetRecipeSuggestionFromCacheUseCase;
import com.gastromind.api.application.usecases.SuggestRecipeFromHouseholdUseCase;
import com.gastromind.api.application.usecases.SuggestRecipeFromHouseholdUseCase.SuggestRecipeResult;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Category;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.Recipe;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.category.CategoryRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.product.ProductBatchRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.product.ProductRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.RecipeResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.recipe.SuggestRecipeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.unit.UnitRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.CategoryRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.ProductRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.RecipeRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.StoreRestMapper;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UnitRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CatalogAndSuggestionControllersTest {

    @Test
    void categoryProductStoreUnitRecipeCrud_shouldUseServiceAndMapper() {
        testCategoryController();
        testProductController();
        testStoreController();
        testUnitController();
        testRecipeController();
    }

    @Test
    void recipeSuggestionController_shouldCoverSuggestAndCachePaths() {
        SuggestRecipeFromHouseholdUseCase suggestUseCase = mock(SuggestRecipeFromHouseholdUseCase.class);
        GetRecipeSuggestionFromCacheUseCase getUseCase = mock(GetRecipeSuggestionFromCacheUseCase.class);
        UserServiceImpl userService = mock(UserServiceImpl.class);
        RecipeRestMapper recipeMapper = mock(RecipeRestMapper.class);
        Authentication auth = mock(Authentication.class);
        RecipeSuggestionController c = new RecipeSuggestionController();
        ReflectionTestUtils.setField(c, "suggestRecipeFromHouseholdUseCase", suggestUseCase);
        ReflectionTestUtils.setField(c, "getRecipeSuggestionFromCacheUseCase", getUseCase);
        ReflectionTestUtils.setField(c, "userServiceImpl", userService);
        ReflectionTestUtils.setField(c, "recipeRestMapper", recipeMapper);

        User user = new User();
        user.setId("u-1");
        HouseHold household = new HouseHold();
        household.setId("h-1");
        user.setHouseHold_id(household);
        when(auth.getName()).thenReturn("owner");
        when(userService.findByUsername("owner")).thenReturn(user);

        Recipe recipe = new Recipe();
        RecipeResponse recipeResponse = mock(RecipeResponse.class);
        when(suggestUseCase.execute("h-1", "u-1", 2)).thenReturn(new SuggestRecipeResult(recipe, "s-1"));
        when(recipeMapper.toResponse(recipe)).thenReturn(recipeResponse);
        assertEquals(HttpStatus.OK, c.suggest(auth, new SuggestRecipeRequest(2)).getStatusCode());

        when(getUseCase.execute("s-1", "h-1", "u-1")).thenReturn(Optional.of(recipe));
        assertEquals(HttpStatus.OK, c.getSuggestion(auth, "s-1").getStatusCode());

        when(getUseCase.execute("s-404", "h-1", "u-1")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> c.getSuggestion(auth, "s-404"));

        User noHouse = new User();
        noHouse.setId("u-2");
        when(userService.findByUsername("owner2")).thenReturn(noHouse);
        when(auth.getName()).thenReturn("owner2");
        assertThrows(ForbiddenException.class, () -> c.suggest(auth, null));
        verify(userService).findByUsername("owner2");
    }

    private static void testCategoryController() {
        CategoryServiceImpl service = mock(CategoryServiceImpl.class);
        CategoryRestMapper mapper = mock(CategoryRestMapper.class);
        CategoryController c = new CategoryController();
        ReflectionTestUtils.setField(c, "categoryServiceImpl", service);
        ReflectionTestUtils.setField(c, "categoryMapper", mapper);
        Category domain = new Category();
        when(service.findAll()).thenReturn(List.of(domain));
        when(mapper.toResponseList(List.of(domain))).thenReturn(List.of(mock(com.gastromind.api.infrastructure.adapters.in.rest.dtos.category.CategoryResponse.class)));
        when(service.findById("c-1")).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(mock(com.gastromind.api.infrastructure.adapters.in.rest.dtos.category.CategoryResponse.class));
        CategoryRequest req = mock(CategoryRequest.class);
        when(mapper.toDomain(req)).thenReturn(domain);
        when(service.create(domain)).thenReturn(domain);
        when(service.update("c-1", domain)).thenReturn(domain);
        assertEquals(HttpStatus.OK, c.getAll().getStatusCode());
        assertEquals(HttpStatus.OK, c.getById("c-1").getStatusCode());
        assertEquals(HttpStatus.CREATED, c.create(req).getStatusCode());
        assertEquals(HttpStatus.OK, c.update("c-1", req).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, c.delete("c-1").getStatusCode());
    }

    private static void testProductController() {
        ProductServiceImpl service = mock(ProductServiceImpl.class);
        ProductRestMapper mapper = mock(ProductRestMapper.class);
        ProductController c = new ProductController();
        ReflectionTestUtils.setField(c, "productServiceImpl", service);
        ReflectionTestUtils.setField(c, "productMapper", mapper);
        Product domain = new Product();
        when(service.findAll()).thenReturn(List.of(domain));
        when(mapper.toResponseList(List.of(domain))).thenReturn(List.of(mock(com.gastromind.api.infrastructure.adapters.in.rest.dtos.product.ProductResponse.class)));
        when(service.findById("p-1")).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(mock(com.gastromind.api.infrastructure.adapters.in.rest.dtos.product.ProductResponse.class));
        ProductRequest req = mock(ProductRequest.class);
        when(mapper.toDomain(req)).thenReturn(domain);
        when(service.create(domain)).thenReturn(domain);
        when(service.update("p-1", domain)).thenReturn(domain);
        when(service.createBatch(List.of("Leche", "Huevos"))).thenReturn(List.of(domain));
        assertEquals(HttpStatus.OK, c.getAll().getStatusCode());
        assertEquals(HttpStatus.OK, c.getById("p-1").getStatusCode());
        assertEquals(HttpStatus.CREATED, c.create(req).getStatusCode());
        assertEquals(HttpStatus.CREATED, c.createBatch(new ProductBatchRequest(List.of("Leche", "Huevos"))).getStatusCode());
        assertEquals(HttpStatus.OK, c.update("p-1", req).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, c.delete("p-1").getStatusCode());
    }

    private static void testStoreController() {
        StoreServiceImpl service = mock(StoreServiceImpl.class);
        StoreRestMapper mapper = mock(StoreRestMapper.class);
        StoreController c = new StoreController();
        ReflectionTestUtils.setField(c, "storeServiceImpl", service);
        ReflectionTestUtils.setField(c, "storeMapper", mapper);
        Store domain = new Store();
        when(service.findAll()).thenReturn(List.of(domain));
        when(mapper.toResponseList(List.of(domain))).thenReturn(List.of(mock(com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreResponse.class)));
        when(service.findById("s-1")).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(mock(com.gastromind.api.infrastructure.adapters.in.rest.dtos.store.StoreResponse.class));
        StoreRequest req = mock(StoreRequest.class);
        when(mapper.toDomain(req)).thenReturn(domain);
        when(service.create(domain)).thenReturn(domain);
        when(service.update("s-1", domain)).thenReturn(domain);
        assertEquals(HttpStatus.OK, c.getAll().getStatusCode());
        assertEquals(HttpStatus.OK, c.getById("s-1").getStatusCode());
        assertEquals(HttpStatus.CREATED, c.create(req).getStatusCode());
        assertEquals(HttpStatus.OK, c.update("s-1", req).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, c.delete("s-1").getStatusCode());
    }

    private static void testUnitController() {
        UnitServiceImpl service = mock(UnitServiceImpl.class);
        UnitRestMapper mapper = mock(UnitRestMapper.class);
        UnitController c = new UnitController();
        ReflectionTestUtils.setField(c, "unitServiceImpl", service);
        ReflectionTestUtils.setField(c, "unitMapper", mapper);
        Unit domain = new Unit();
        when(service.findAll()).thenReturn(List.of(domain));
        when(mapper.toResponseList(List.of(domain))).thenReturn(List.of(mock(com.gastromind.api.infrastructure.adapters.in.rest.dtos.unit.UnitResponse.class)));
        when(service.findById("u-1")).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(mock(com.gastromind.api.infrastructure.adapters.in.rest.dtos.unit.UnitResponse.class));
        UnitRequest req = mock(UnitRequest.class);
        when(mapper.toDomain(req)).thenReturn(domain);
        when(service.create(domain)).thenReturn(domain);
        when(service.update("u-1", domain)).thenReturn(domain);
        assertEquals(HttpStatus.OK, c.getAll().getStatusCode());
        assertEquals(HttpStatus.OK, c.getById("u-1").getStatusCode());
        assertEquals(HttpStatus.CREATED, c.create(req).getStatusCode());
        assertEquals(HttpStatus.OK, c.update("u-1", req).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, c.delete("u-1").getStatusCode());
    }

    private static void testRecipeController() {
        RecipeServiceImpl service = mock(RecipeServiceImpl.class);
        RecipeRestMapper mapper = mock(RecipeRestMapper.class);
        RecipeController c = new RecipeController();
        ReflectionTestUtils.setField(c, "recipeServiceImpl", service);
        ReflectionTestUtils.setField(c, "recipeMapper", mapper);
        Recipe domain = new Recipe();
        when(service.findAll()).thenReturn(List.of(domain));
        when(mapper.toResponseList(List.of(domain))).thenReturn(List.of(mock(RecipeResponse.class)));
        when(service.findById("r-1")).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(mock(RecipeResponse.class));
        RecipeRequest req = mock(RecipeRequest.class);
        when(mapper.toDomain(req)).thenReturn(domain);
        when(service.create(domain)).thenReturn(domain);
        when(service.update("r-1", domain)).thenReturn(domain);
        assertEquals(HttpStatus.OK, c.getAll().getStatusCode());
        assertEquals(HttpStatus.OK, c.getById("r-1").getStatusCode());
        assertEquals(HttpStatus.CREATED, c.create(req).getStatusCode());
        assertEquals(HttpStatus.OK, c.update("r-1", req).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, c.delete("r-1").getStatusCode());
    }
}
