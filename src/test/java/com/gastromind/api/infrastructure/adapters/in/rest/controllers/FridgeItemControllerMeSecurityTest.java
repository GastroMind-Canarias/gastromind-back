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
    void meRoutes_shouldAllowMemberOwnerAndAdmin() throws Exception {
        FridgeItem domain = buildItem("item-1", "fridge-1");
        FridgeItemResponse response = new FridgeItemResponse("item-1", new BigDecimal("2.0"), LocalDate.of(2026, 5, 1), "IN_FRIDGE", "Leche", "fridge-1");
        String body = objectMapper.writeValueAsString(Map.of(
                "productId", "product-1",
                "fridgeId", "ignored-fridge-id",
                "quantity", "2.0",
                "expirationDate", "2026-05-01",
                "status", "IN_FRIDGE"
        ));
        String consumeBody = objectMapper.writeValueAsString(new BigDecimal("1.0"));

        when(fridgeItemRestMapper.toDomain(any())).thenReturn(domain);
        when(fridgeItemRestMapper.toResponse(any())).thenReturn(response);
        when(fridgeItemRestMapper.toResponseList(any())).thenReturn(List.of(response));

        when(listMyFridgeItemsUseCase.execute(any())).thenReturn(List.of(domain));
        when(createMyFridgeItemUseCase.execute(any(), eq("product-1"), eq(new BigDecimal("2.0")), eq(LocalDate.of(2026, 5, 1)))).thenReturn(domain);
        when(updateMyFridgeItemUseCase.execute(any(), eq("item-1"), any())).thenReturn(domain);
        when(consumeMyFridgeItemUseCase.execute(any(), eq("item-1"), eq(new BigDecimal("1.0")))).thenReturn(domain);
        when(listMyExpiringFridgeItemsUseCase.execute(any(), eq(5))).thenReturn(List.of(domain));
        when(listMyFridgeItemsByCategoryUseCase.execute(any(), eq("cat-1"))).thenReturn(List.of(domain));

        String[] roles = new String[]{"ROLE_MEMBER", "ROLE_OWNER", "ROLE_ADMIN"};
        for (String role : roles) {
            mockMvc.perform(get("/api/v1/fridge-items/me")
                            .with(user("user1").authorities(new SimpleGrantedAuthority(role))))
                    .andExpect(status().isOk());

            mockMvc.perform(post("/api/v1/fridge-items/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
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

    private FridgeItem buildItem(String itemId, String fridgeId) {
        FridgeItem item = new FridgeItem();
        item.setId(itemId);
        item.setFridgeId(fridgeId);
        item.setQuantity(new BigDecimal("2.0"));
        item.setExpirationDate(LocalDate.of(2026, 5, 1));
        return item;
    }
}
