package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.AllergenServiceImpl;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.AllergenRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AllergenControllerTest {

    @Test
    void crud_flow_usesServiceAndMapper() {
        AllergenServiceImpl service = mock(AllergenServiceImpl.class);
        AllergenRestMapper mapper = mock(AllergenRestMapper.class);
        AllergenController c = new AllergenController();
        ReflectionTestUtils.setField(c, "allergenServiceImpl", service);
        ReflectionTestUtils.setField(c, "allergenMapper", mapper);

        Allergen a = new Allergen("a1", "Gluten");
        when(service.findAll()).thenReturn(List.of(a));
        when(mapper.toResponseList(List.of(a))).thenReturn(List.of(new AllergenResponse("a1", "Gluten")));
        assertEquals(HttpStatus.OK, c.getAll().getStatusCode());
        assertEquals(1, c.getAll().getBody().size());

        when(service.findById("a1")).thenReturn(a);
        when(mapper.toResponse(a)).thenReturn(new AllergenResponse("a1", "Gluten"));
        assertEquals("Gluten", c.getById("a1").getBody().name());

        AllergenRequest req = new AllergenRequest("Nuevo");
        when(mapper.toDomain(req)).thenReturn(new Allergen(null, "Nuevo"));
        when(service.create(any())).thenReturn(new Allergen("n1", "Nuevo"));
        when(mapper.toResponse(any(Allergen.class))).thenReturn(new AllergenResponse("n1", "Nuevo"));
        assertEquals(HttpStatus.CREATED, c.create(req).getStatusCode());

        when(mapper.toDomain(req)).thenReturn(new Allergen(null, "Nuevo"));
        when(service.update(any(), any())).thenReturn(new Allergen("a1", "Nuevo"));
        when(mapper.toResponse(any(Allergen.class))).thenReturn(new AllergenResponse("a1", "Nuevo"));
        assertEquals(HttpStatus.OK, c.update("a1", req).getStatusCode());

        c.delete("a1");
        verify(service).delete("a1");
    }
}
