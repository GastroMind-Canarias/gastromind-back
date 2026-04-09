package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gastromind.api.application.services.FridgeItemServiceImpl;
import com.gastromind.api.application.usecases.ConsumeMyFridgeItemUseCase;
import com.gastromind.api.application.usecases.CreateMyFridgeItemUseCase;
import com.gastromind.api.application.usecases.DeleteMyFridgeItemUseCase;
import com.gastromind.api.application.usecases.ListMyExpiringFridgeItemsUseCase;
import com.gastromind.api.application.usecases.ListMyFridgeItemsByCategoryUseCase;
import com.gastromind.api.application.usecases.ListMyFridgeItemsUseCase;
import com.gastromind.api.application.usecases.MarkMyFridgeItemConsumedUseCase;
import com.gastromind.api.application.usecases.UpdateMyFridgeItemUseCase;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.FridgeItemResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.handler.GlobalExceptionHandler;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.FridgeItemRestMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UserJpaRepository;
import com.gastromind.api.infrastructure.security.config.SecurityConfig;
import com.gastromind.api.infrastructure.security.config.SecurityPathsProperties;
import com.gastromind.api.infrastructure.security.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FridgeItemController.class)
@Import({SecurityConfig.class, SecurityPathsProperties.class, GlobalExceptionHandler.class})
class FridgeItemControllerAdminSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FridgeItemServiceImpl fridgeItemService;

    @MockBean
    private FridgeItemRestMapper fridgeItemRestMapper;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserJpaRepository userJpaRepository;

    @MockBean
    private ListMyFridgeItemsUseCase listMyFridgeItemsUseCase;
    @MockBean
    private CreateMyFridgeItemUseCase createMyFridgeItemUseCase;
    @MockBean
    private UpdateMyFridgeItemUseCase updateMyFridgeItemUseCase;
    @MockBean
    private DeleteMyFridgeItemUseCase deleteMyFridgeItemUseCase;
    @MockBean
    private ConsumeMyFridgeItemUseCase consumeMyFridgeItemUseCase;
    @MockBean
    private MarkMyFridgeItemConsumedUseCase markMyFridgeItemConsumedUseCase;
    @MockBean
    private ListMyExpiringFridgeItemsUseCase listMyExpiringFridgeItemsUseCase;
    @MockBean
    private ListMyFridgeItemsByCategoryUseCase listMyFridgeItemsByCategoryUseCase;

    @BeforeEach
    void setUpFilterPassThrough() throws Exception {
        doAnswer(invocation -> {
            jakarta.servlet.FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    void adminCanUseAllNonMeRoutes() throws Exception {
        FridgeItem domain = buildItem("item-1", "fridge-1");
        FridgeItemResponse response = new FridgeItemResponse(
                "item-1",
                new BigDecimal("2.0"),
                LocalDate.of(2026, 5, 1),
                "IN_FRIDGE",
                "Leche",
                "fridge-1");
        String body = objectMapper.writeValueAsString(Map.of(
                "productId", "product-1",
                "fridgeId", "fridge-1",
                "quantity", "2.0",
                "expirationDate", "2026-05-01",
                "status", "IN_FRIDGE"
        ));
        String consumeBody = objectMapper.writeValueAsString(new BigDecimal("1.0"));

        when(fridgeItemService.findAll()).thenReturn(List.of(domain));
        when(fridgeItemService.findById("item-1")).thenReturn(domain);
        when(fridgeItemService.findByFridgeId("fridge-1")).thenReturn(List.of(domain));
        when(fridgeItemService.addProductToFridge(eq("fridge-1"), eq("product-1"), eq(new BigDecimal("2.0")), eq(LocalDate.of(2026, 5, 1))))
                .thenReturn(domain);
        when(fridgeItemService.update(eq("item-1"), any())).thenReturn(domain);
        when(fridgeItemService.consumePartially(eq("item-1"), eq(new BigDecimal("1.0")))).thenReturn(domain);
        when(fridgeItemService.getExpiringItems(eq("fridge-1"), eq(5))).thenReturn(List.of(domain));
        when(fridgeItemService.getInventoryByCategory(eq("fridge-1"), eq("cat-1"))).thenReturn(List.of(domain));
        when(fridgeItemRestMapper.toDomain(any())).thenReturn(domain);
        when(fridgeItemRestMapper.toResponse(any())).thenReturn(response);
        when(fridgeItemRestMapper.toResponseList(any())).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/fridge-items")
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/fridge-items/item-1")
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/fridge-items/fridge/fridge-1")
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/fridge-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/v1/fridge-items/item-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1/fridge-items/item-1/consume")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(consumeBody)
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1/fridge-items/item-1/mark-consumed")
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/fridge-items/fridge/fridge-1/expiring")
                        .param("days", "5")
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/fridge-items/fridge/fridge-1/category/cat-1")
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/fridge-items/item-1")
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());

        verify(fridgeItemService).findAll();
        verify(fridgeItemService).findById("item-1");
        verify(fridgeItemService).findByFridgeId("fridge-1");
        verify(fridgeItemService).addProductToFridge(eq("fridge-1"), eq("product-1"), eq(new BigDecimal("2.0")), eq(LocalDate.of(2026, 5, 1)));
        verify(fridgeItemService).update(eq("item-1"), any());
        verify(fridgeItemService).consumePartially(eq("item-1"), eq(new BigDecimal("1.0")));
        verify(fridgeItemService).markAsConsumed("item-1");
        verify(fridgeItemService).getExpiringItems("fridge-1", 5);
        verify(fridgeItemService).getInventoryByCategory("fridge-1", "cat-1");
        verify(fridgeItemService).delete("item-1");
    }

    @Test
    void memberAndOwnerCannotUseAnyNonMeRoutes() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "productId", "product-1",
                "fridgeId", "fridge-1",
                "quantity", "2.0",
                "expirationDate", "2026-05-01",
                "status", "IN_FRIDGE"
        ));
        String consumeBody = objectMapper.writeValueAsString(new BigDecimal("1.0"));
        String[] roles = new String[]{"ROLE_MEMBER", "ROLE_OWNER"};

        for (String role : roles) {
            mockMvc.perform(get("/api/v1/fridge-items")
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/v1/fridge-items/item-1")
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/v1/fridge-items/fridge/fridge-1")
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isForbidden());

            mockMvc.perform(post("/api/v1/fridge-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isForbidden());

            mockMvc.perform(put("/api/v1/fridge-items/item-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isForbidden());

            mockMvc.perform(put("/api/v1/fridge-items/item-1/consume")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(consumeBody)
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isForbidden());

            mockMvc.perform(put("/api/v1/fridge-items/item-1/mark-consumed")
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/v1/fridge-items/fridge/fridge-1/expiring")
                            .param("days", "5")
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/v1/fridge-items/fridge/fridge-1/category/cat-1")
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isForbidden());

            mockMvc.perform(delete("/api/v1/fridge-items/item-1")
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isForbidden());
        }
    }

    private FridgeItem buildItem(String itemId, String fridgeId) {
        FridgeItem item = new FridgeItem();
        item.setId(itemId);
        item.setFridgeId(fridgeId);
        item.setQuantity(new BigDecimal("2.0"));
        item.setExpirationDate(LocalDate.of(2026, 5, 1));
        return item;
    }
}
