package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Category;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemProductSummaryResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemResponse;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class FridgeItemRestMapperTest {

    private final FridgeItemRestMapper mapper = new FridgeItemRestMapperImpl();

    @Test
    void toResponse_includesProductSummaryWhenCatalogProduct() {
        Product p = new Product("p-1", "Leche", false, null);
        p.setCategory(new Category("c-1", "Lácteos"));
        p.setNeedsReview(true);
        FridgeItem item = new FridgeItem("i-1", new BigDecimal("1.5"), LocalDate.of(2026, 6, 1), ItemStatus.IN_FRIDGE, p, "f-1");

        FridgeItemResponse out = mapper.toResponse(item);

        assertEquals("Leche", out.productName());
        assertNotNull(out.product());
        FridgeItemProductSummaryResponse s = out.product();
        assertEquals("p-1", s.id());
        assertEquals("Leche", s.name());
        assertEquals("c-1", s.categoryId());
        assertEquals("Lácteos", s.categoryName());
        assertEquals(false, s.isEssential());
        assertEquals(true, s.needsReview());
    }

    @Test
    void toResponse_labelOnlyItem_hasNullProductBlock() {
        FridgeItem item = new FridgeItem();
        item.setId("i-2");
        item.setQuantity(BigDecimal.ONE);
        item.setExpirationDate(LocalDate.of(2026, 1, 1));
        item.setStatus(ItemStatus.IN_FRIDGE);
        item.setFridgeId("f-1");
        item.setProductLabel("Algo raro del mercado");

        FridgeItemResponse out = mapper.toResponse(item);

        assertEquals("Algo raro del mercado", out.productName());
        assertNull(out.product());
    }

    @Test
    void toResponseList_mapsEach() {
        Product p = new Product("p-1");
        p.setName("Huevos");
        FridgeItem item = new FridgeItem("i-1", BigDecimal.ONE, LocalDate.now(), ItemStatus.IN_FRIDGE, p, "f-1");
        List<FridgeItemResponse> list = mapper.toResponseList(List.of(item));
        assertEquals(1, list.size());
        assertEquals("Huevos", list.get(0).productName());
        assertEquals("p-1", list.get(0).product().id());
    }
}
