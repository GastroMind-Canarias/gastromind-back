package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.UserFavoritesServiceImpl;
import com.gastromind.api.application.services.UserServiceImpl;
import com.gastromind.api.application.usecases.SaveSuggestedRecipeAsFavoriteUseCase;
import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.UserFavorites;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites.UserFavoritesMeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites.UserFavoritesRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.userfavorites.UserFavoritesResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.UserFavoritesRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserFavoritesControllerTest {

    @Test
    void crudMineAndSuggestionSave_shouldDelegateAndMap() {
        UserFavoritesServiceImpl service = mock(UserFavoritesServiceImpl.class);
        UserFavoritesRestMapper mapper = mock(UserFavoritesRestMapper.class);
        SaveSuggestedRecipeAsFavoriteUseCase saveUseCase = mock(SaveSuggestedRecipeAsFavoriteUseCase.class);
        UserServiceImpl userService = mock(UserServiceImpl.class);
        Authentication auth = mock(Authentication.class);
        UserFavoritesController c = buildController(service, mapper, saveUseCase, userService);

        User user = new User();
        user.setId("u-1");
        HouseHold household = new HouseHold();
        household.setId("h-1");
        user.setHouseHold_id(household);
        when(auth.getName()).thenReturn("owner");
        when(userService.findByUsername("owner")).thenReturn(user);

        UserFavorites fav = new UserFavorites();
        UserFavoritesResponse response = mock(UserFavoritesResponse.class);
        when(service.findAll()).thenReturn(List.of(fav));
        when(service.findAllByUserId("u-1")).thenReturn(List.of(fav));
        when(service.findById("f-1")).thenReturn(fav);
        when(service.findByIdForUser("f-1", "u-1")).thenReturn(fav);
        when(mapper.toResponseList(List.of(fav))).thenReturn(List.of(response));
        when(mapper.toResponse(fav)).thenReturn(response);

        assertEquals(HttpStatus.OK, c.getAll().getStatusCode());
        assertEquals(HttpStatus.OK, c.listMine(auth).getStatusCode());
        assertEquals(HttpStatus.OK, c.getById("f-1").getStatusCode());
        assertEquals(HttpStatus.OK, c.getMineById(auth, "f-1").getStatusCode());

        UserFavoritesRequest req = mock(UserFavoritesRequest.class);
        UserFavoritesMeRequest meReq = mock(UserFavoritesMeRequest.class);
        when(mapper.toDomain(req)).thenReturn(fav);
        when(mapper.toDomainForMe(meReq, "u-1")).thenReturn(fav);
        when(service.create(fav)).thenReturn(fav);
        when(service.update(eq("f-1"), any(UserFavorites.class))).thenReturn(fav);
        when(service.updateForUser(eq("f-1"), any(UserFavorites.class), eq("u-1"))).thenReturn(fav);
        when(saveUseCase.execute("s-1", "h-1", "u-1", user)).thenReturn(fav);

        assertEquals(HttpStatus.CREATED, c.create(req).getStatusCode());
        assertEquals(HttpStatus.CREATED, c.createMine(auth, meReq).getStatusCode());
        assertEquals(HttpStatus.OK, c.update("f-1", req).getStatusCode());
        assertEquals(HttpStatus.OK, c.updateMine(auth, "f-1", meReq).getStatusCode());
        assertEquals(HttpStatus.CREATED, c.saveFromSuggestion(auth, "s-1").getStatusCode());

        assertEquals(HttpStatus.NO_CONTENT, c.delete("f-1").getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, c.deleteMine(auth, "f-1").getStatusCode());
        verify(service).deleteForUser("f-1", "u-1");
    }

    @Test
    void saveFromSuggestion_shouldFailWhenUserWithoutHousehold() {
        UserFavoritesController c = buildController(
                mock(UserFavoritesServiceImpl.class),
                mock(UserFavoritesRestMapper.class),
                mock(SaveSuggestedRecipeAsFavoriteUseCase.class),
                mock(UserServiceImpl.class));
        Authentication auth = mock(Authentication.class);
        User user = new User();
        user.setId("u-1");
        when(auth.getName()).thenReturn("owner");
        UserServiceImpl userService = (UserServiceImpl) ReflectionTestUtils.getField(c, "userServiceImpl");
        when(userService.findByUsername("owner")).thenReturn(user);

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> c.saveFromSuggestion(auth, "s-1"));
        assertEquals("El usuario no pertenece a ningún hogar", ex.getMessage());
    }

    private static UserFavoritesController buildController(
            UserFavoritesServiceImpl service,
            UserFavoritesRestMapper mapper,
            SaveSuggestedRecipeAsFavoriteUseCase saveUseCase,
            UserServiceImpl userService) {
        UserFavoritesController c = new UserFavoritesController();
        ReflectionTestUtils.setField(c, "userFavoritesServiceImpl", service);
        ReflectionTestUtils.setField(c, "favoritesMapper", mapper);
        ReflectionTestUtils.setField(c, "saveSuggestedRecipeAsFavoriteUseCase", saveUseCase);
        ReflectionTestUtils.setField(c, "userServiceImpl", userService);
        return c;
    }
}
