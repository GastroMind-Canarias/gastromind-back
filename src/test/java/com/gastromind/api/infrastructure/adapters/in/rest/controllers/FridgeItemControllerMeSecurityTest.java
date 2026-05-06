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
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridgeItem.MyFridgeItemRequest;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FridgeItemController.class)
@Import({SecurityConfig.class, SecurityPathsProperties.class, GlobalExceptionHandler.class})
class FridgeItemControllerMeSecurityTest {

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
    void meRoutes_shouldAllowOwnerMemberAndAdmin() throws Exception {
        FridgeItem domain = buildItem("item-1", "fridge-1");
        FridgeItemResponse response = new FridgeItemResponse("item-1", new BigDecimal("2.0"), LocalDate.of(2026, 5, 1), "IN_FRIDGE", "Leche");
        String body = objectMapper.writeValueAsString(Map.of(
                "productId", "product-1",
                "quantity", "2.0",
                "expirationDate", "2026-05-01",
                "status", "IN_FRIDGE"
        ));
        String bodyWithProductNameOnly = objectMapper.writeValueAsString(Map.of(
                "productName", "Leche",
                "quantity", "2.0",
                "expirationDate", "2026-05-01",
                "status", "IN_FRIDGE"
        ));
        String batchBody = objectMapper.writeValueAsString(Map.of(
                "items", List.of(
                        Map.of(
                                "productId", "product-1",
                                "quantity", "2.0",
                                "expirationDate", "2026-05-01",
                                "status", "IN_FRIDGE"
                        ),
                        Map.of(
                                "productName", "Leche",
                                "quantity", "1.0",
                                "expirationDate", "2026-05-02",
                                "status", "GOOD"
                        )
                )
        ));
        String consumeBody = objectMapper.writeValueAsString(new BigDecimal("1.0"));

        when(fridgeItemRestMapper.toDomain(any(MyFridgeItemRequest.class))).thenReturn(domain);
        when(fridgeItemRestMapper.toResponse(any())).thenReturn(response);
        when(fridgeItemRestMapper.toResponseList(any())).thenReturn(List.of(response));

        when(listMyFridgeItemsUseCase.execute(any())).thenReturn(List.of(domain));
        when(createMyFridgeItemUseCase.execute(
                any(),
                eq("product-1"),
                eq(null),
                eq(new BigDecimal("2.0")),
                eq(LocalDate.of(2026, 5, 1)),
                eq(ItemStatus.IN_FRIDGE)))
                .thenReturn(domain);
        when(createMyFridgeItemUseCase.execute(
                any(),
                eq(null),
                eq("Leche"),
                eq(new BigDecimal("2.0")),
                eq(LocalDate.of(2026, 5, 1)),
                eq(ItemStatus.IN_FRIDGE)))
                .thenReturn(domain);
        when(createMyFridgeItemUseCase.execute(
                any(),
                eq(null),
                eq("Leche"),
                eq(new BigDecimal("1.0")),
                eq(LocalDate.of(2026, 5, 2)),
                eq(ItemStatus.GOOD)))
                .thenReturn(domain);
        when(updateMyFridgeItemUseCase.execute(any(), eq("item-1"), any())).thenReturn(domain);
        when(consumeMyFridgeItemUseCase.execute(any(), eq("item-1"), eq(new BigDecimal("1.0")))).thenReturn(domain);
        when(listMyExpiringFridgeItemsUseCase.execute(any(), eq(5))).thenReturn(List.of(domain));
        when(listMyFridgeItemsByCategoryUseCase.execute(any(), eq("cat-1"))).thenReturn(List.of(domain));

        String[] roles = new String[]{"ROLE_OWNER", "ROLE_MEMBER", "ROLE_ADMIN"};
        for (String role : roles) {
            mockMvc.perform(get("/api/v1/fridge-items/me")
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isOk());

            mockMvc.perform(post("/api/v1/fridge-items/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/v1/fridge-items/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(bodyWithProductNameOnly)
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/v1/fridge-items/me/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(batchBody)
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isCreated());

            mockMvc.perform(put("/api/v1/fridge-items/me/item-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isOk());

            mockMvc.perform(put("/api/v1/fridge-items/me/item-1/consume")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(consumeBody)
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isOk());

            mockMvc.perform(put("/api/v1/fridge-items/me/item-1/mark-consumed")
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isOk());

            mockMvc.perform(delete("/api/v1/fridge-items/me/item-1")
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/v1/fridge-items/me/expiring")
                            .param("days", "5")
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/v1/fridge-items/me/category/cat-1")
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void meRoutes_shouldRejectUnauthorizedRole() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "productId", "product-1",
                "quantity", "2.0",
                "expirationDate", "2026-05-01",
                "status", "IN_FRIDGE"
        ));
        String consumeBody = objectMapper.writeValueAsString(new BigDecimal("1.0"));
        String batchBody = objectMapper.writeValueAsString(Map.of(
                "items", List.of(Map.of(
                        "productId", "product-1",
                        "quantity", "2.0",
                        "expirationDate", "2026-05-01",
                        "status", "IN_FRIDGE"
                ))
        ));
        var unauthorized = user("user1").authorities(new SimpleGrantedAuthority("ROLE_USER"));

        mockMvc.perform(get("/api/v1/fridge-items/me").with(unauthorized)).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/fridge-items/me").contentType(MediaType.APPLICATION_JSON).content(body).with(unauthorized))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/fridge-items/me/batch").contentType(MediaType.APPLICATION_JSON).content(batchBody).with(unauthorized))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/v1/fridge-items/me/item-1").contentType(MediaType.APPLICATION_JSON).content(body).with(unauthorized))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/v1/fridge-items/me/item-1/consume").contentType(MediaType.APPLICATION_JSON).content(consumeBody).with(unauthorized))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/v1/fridge-items/me/item-1/mark-consumed").with(unauthorized)).andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/v1/fridge-items/me/item-1").with(unauthorized)).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/fridge-items/me/expiring").param("days", "5").with(unauthorized)).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/fridge-items/me/category/cat-1").with(unauthorized)).andExpect(status().isForbidden());
    }

    @Test
    void createMyItem_shouldReturnBadRequestWhenProductIdAndProductNameMissing() throws Exception {
        String invalidBody = objectMapper.writeValueAsString(Map.of(
                "quantity", "2.0",
                "expirationDate", "2026-05-01",
                "status", "IN_FRIDGE"
        ));

        mockMvc.perform(post("/api/v1/fridge-items/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody)
                        .with(user("user1").authorities(new SimpleGrantedAuthority("ROLE_OWNER"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMyItemsBatch_shouldReturnBadRequestWhenEmpty() throws Exception {
        String invalidBody = objectMapper.writeValueAsString(Map.of("items", List.of()));

        mockMvc.perform(post("/api/v1/fridge-items/me/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody)
                        .with(user("user1").authorities(new SimpleGrantedAuthority("ROLE_OWNER"))))
                .andExpect(status().isBadRequest());
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
