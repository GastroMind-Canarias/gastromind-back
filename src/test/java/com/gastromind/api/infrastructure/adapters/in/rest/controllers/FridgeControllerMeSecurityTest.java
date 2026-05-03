package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gastromind.api.application.services.FridgeServiceImpl;
import com.gastromind.api.application.usecases.CreateMyFridgeUseCase;
import com.gastromind.api.application.usecases.DeleteMyFridgeUseCase;
import com.gastromind.api.application.usecases.GetMyFridgeUseCase;
import com.gastromind.api.application.usecases.UpdateMyFridgeUseCase;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.fridge.FridgeResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.handler.GlobalExceptionHandler;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.FridgeRestMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UserJpaRepository;
import com.gastromind.api.infrastructure.security.config.SecurityConfig;
import com.gastromind.api.infrastructure.security.config.SecurityPathsProperties;
import com.gastromind.api.infrastructure.security.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(controllers = FridgeController.class)
@Import({SecurityConfig.class, SecurityPathsProperties.class, GlobalExceptionHandler.class})
class FridgeControllerMeSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FridgeServiceImpl fridgeServiceImpl;

    @MockBean
    private FridgeRestMapper fridgeRestMapper;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserJpaRepository userJpaRepository;

    @MockBean
    private GetMyFridgeUseCase getMyFridgeUseCase;

    @MockBean
    private CreateMyFridgeUseCase createMyFridgeUseCase;

    @MockBean
    private UpdateMyFridgeUseCase updateMyFridgeUseCase;

    @MockBean
    private DeleteMyFridgeUseCase deleteMyFridgeUseCase;

    @BeforeEach
    void setUpFilterPassThrough() throws Exception {
        doAnswer(invocation -> {
            jakarta.servlet.FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    void memberCanGetMeButCannotMutate() throws Exception {
        Fridge fridge = buildFridge("fridge-1", "house-1");
        FridgeResponse response = new FridgeResponse("fridge-1", "house-1");

        when(getMyFridgeUseCase.execute("member1")).thenReturn(fridge);
        when(fridgeRestMapper.toResponse(fridge)).thenReturn(response);

        mockMvc.perform(get("/api/v1/fridges/me")
                        .with(user("member1").authorities(new SimpleGrantedAuthority("ROLE_MEMBER"))))
                .andExpect(status().isOk());

        String body = objectMapper.writeValueAsString(java.util.Map.of("household_id", "house-1"));

        mockMvc.perform(post("/api/v1/fridges/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(user("member1").authorities(new SimpleGrantedAuthority("ROLE_MEMBER"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/v1/fridges/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(user("member1").authorities(new SimpleGrantedAuthority("ROLE_MEMBER"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/fridges/me")
                        .with(user("member1").authorities(new SimpleGrantedAuthority("ROLE_MEMBER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanGetMeButCannotMutateMeRoutes() throws Exception {
        Fridge fridge = buildFridge("fridge-1", "house-1");
        FridgeResponse response = new FridgeResponse("fridge-1", "house-1");

        when(getMyFridgeUseCase.execute("admin1")).thenReturn(fridge);
        when(fridgeRestMapper.toResponse(fridge)).thenReturn(response);

        mockMvc.perform(get("/api/v1/fridges/me")
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        String body = objectMapper.writeValueAsString(java.util.Map.of("household_id", "house-1"));

        mockMvc.perform(post("/api/v1/fridges/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/v1/fridges/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/fridges/me")
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void ownerCanCreateUpdateDeleteMyFridge() throws Exception {
        Fridge domain = buildFridge("fridge-1", "house-1");
        FridgeResponse response = new FridgeResponse("fridge-1", "house-1");

        when(fridgeRestMapper.toDomain(any())).thenReturn(domain);
        when(createMyFridgeUseCase.execute(eq("owner1"), any())).thenReturn(domain);
        when(updateMyFridgeUseCase.execute(eq("owner1"), any())).thenReturn(domain);
        when(fridgeRestMapper.toResponse(domain)).thenReturn(response);

        String body = objectMapper.writeValueAsString(java.util.Map.of("household_id", "house-1"));

        mockMvc.perform(post("/api/v1/fridges/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(user("owner1").authorities(new SimpleGrantedAuthority("ROLE_OWNER"))))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/v1/fridges/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(user("owner1").authorities(new SimpleGrantedAuthority("ROLE_OWNER"))))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/fridges/me")
                        .with(user("owner1").authorities(new SimpleGrantedAuthority("ROLE_OWNER"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void adminCanUseGlobalAndIdCrudRoutes() throws Exception {
        Fridge fridge = buildFridge("fridge-77", "house-1");
        FridgeResponse response = new FridgeResponse("fridge-77", "house-1");
        String body = objectMapper.writeValueAsString(java.util.Map.of("household_id", "house-1"));

        when(fridgeServiceImpl.findAll()).thenReturn(java.util.List.of(fridge));
        when(fridgeServiceImpl.findById("fridge-77")).thenReturn(fridge);
        when(fridgeRestMapper.toDomain(any())).thenReturn(fridge);
        when(fridgeServiceImpl.create(any())).thenReturn(fridge);
        when(fridgeServiceImpl.update(eq("fridge-77"), any())).thenReturn(fridge);
        when(fridgeRestMapper.toResponse(fridge)).thenReturn(response);
        when(fridgeRestMapper.toResponseList(java.util.List.of(fridge))).thenReturn(java.util.List.of(response));

        mockMvc.perform(get("/api/v1/fridges")
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/fridges/fridge-77")
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/fridges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/v1/fridges/fridge-77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/fridges/fridge-77")
                        .with(user("admin1").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());

        verify(fridgeServiceImpl).findAll();
        verify(fridgeServiceImpl).findById("fridge-77");
        verify(fridgeServiceImpl).create(any());
        verify(fridgeServiceImpl).update(eq("fridge-77"), any());
        verify(fridgeServiceImpl).delete("fridge-77");
    }

    @Test
    void memberCannotUseGlobalOrIdRoutes() throws Exception {
        String body = objectMapper.writeValueAsString(java.util.Map.of("household_id", "house-1"));

        mockMvc.perform(get("/api/v1/fridges")
                        .with(user("member1").authorities(new SimpleGrantedAuthority("ROLE_MEMBER"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/fridges/fridge-77")
                        .with(user("member1").authorities(new SimpleGrantedAuthority("ROLE_MEMBER"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/fridges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(user("member1").authorities(new SimpleGrantedAuthority("ROLE_MEMBER"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/v1/fridges/fridge-77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(user("member1").authorities(new SimpleGrantedAuthority("ROLE_MEMBER"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/fridges/fridge-77")
                        .with(user("member1").authorities(new SimpleGrantedAuthority("ROLE_MEMBER"))))
                .andExpect(status().isForbidden());
    }

    private Fridge buildFridge(String fridgeId, String householdId) {
        Fridge fridge = new Fridge();
        fridge.setId(fridgeId);
        HouseHold houseHold = new HouseHold();
        houseHold.setId(householdId);
        fridge.setHouseHold_id(houseHold);
        return fridge;
    }
}
