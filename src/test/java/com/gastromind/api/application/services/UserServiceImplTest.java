package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.out.AllergenRepository;
import com.gastromind.api.domain.ports.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AllergenRepository allergenRepository;

    @InjectMocks
    private UserServiceImpl service;

    private User existing;

    @BeforeEach
    void setUp() {
        existing = new User("u-1");
        existing.setName("n");
        existing.setEmail("e@e.com");
        existing.setAllergens(new HashSet<>());
    }

    @Test
    void findAll_findById_findByUsername() {
        when(userRepository.findAll()).thenReturn(List.of(existing));
        assertEquals(List.of(existing), service.findAll());

        when(userRepository.findById("u-1")).thenReturn(Optional.of(existing));
        assertEquals(existing, service.findById("u-1"));

        when(userRepository.findByName("n")).thenReturn(Optional.of(existing));
        assertEquals(existing, service.findByUsername("n"));
    }

    @Test
    void findById_throwsWhenMissing() {
        when(userRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById("x"));
    }

    @Test
    void create_update_delete() {
        when(userRepository.save(existing)).thenReturn(existing);
        assertEquals(existing, service.create(existing));

        when(userRepository.findById("u-1")).thenReturn(Optional.of(existing));
        User patch = new User();
        when(userRepository.save(patch)).thenReturn(patch);
        assertEquals(patch, service.update("u-1", patch));

        when(userRepository.findById("u-1")).thenReturn(Optional.of(existing));
        service.delete("u-1");
        verify(userRepository).deleteById(eq("u-1"));
    }

    @Test
    void updateProfile_replacesAllergensWhenProvided() {
        Allergen a1 = new Allergen("a-1", "Gluten");
        User changes = new User();
        changes.setName("new");
        changes.setEmail("n2@n2.com");
        changes.setAllergens(Set.of(new Allergen("a-1")));

        when(userRepository.findById("u-1")).thenReturn(Optional.of(existing));
        when(allergenRepository.findById("a-1")).thenReturn(Optional.of(a1));
        when(userRepository.save(existing)).thenReturn(existing);

        User out = service.updateProfile("u-1", changes);

        assertEquals("new", out.getName());
        assertEquals(1, out.getAllergens().size());
    }

    @Test
    void addAllergen_and_removeAllergen() {
        Allergen a1 = new Allergen("a-1", "Gluten");
        when(userRepository.findById("u-1")).thenReturn(Optional.of(existing));
        when(allergenRepository.findById("a-1")).thenReturn(Optional.of(a1));
        when(userRepository.save(existing)).thenReturn(existing);

        service.addAllergen("u-1", "a-1");
        verify(userRepository).save(existing);

        when(userRepository.findById("u-1")).thenReturn(Optional.of(existing));
        service.removeAllergen("u-1", "a-1");
        verify(userRepository, times(2)).save(existing);
    }

    @Test
    void addAllergensBulk_skipsNullAndEmpty() {
        service.addAllergensBulk("u-1", null);
        service.addAllergensBulk("u-1", List.of());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void addAllergensBulk_dedupesAndSkipsBlanks() {
        Allergen a1 = new Allergen("a-1", "Gluten");
        when(userRepository.findById("u-1")).thenReturn(Optional.of(existing));
        when(allergenRepository.findById("a-1")).thenReturn(Optional.of(a1));
        when(userRepository.save(existing)).thenReturn(existing);

        service.addAllergensBulk("u-1", Arrays.asList("a-1", "a-1", " ", null));

        verify(userRepository, times(1)).save(existing);
    }

    @Test
    void removeAllergensBulk_nullList_noOp() {
        service.removeAllergensBulk("u-1", null);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void replaceAllergens_clearsThenBulkAdds() {
        when(userRepository.findById("u-1")).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        Allergen a1 = new Allergen("a-1", "Gluten");
        when(allergenRepository.findById("a-1")).thenReturn(Optional.of(a1));

        service.replaceAllergens("u-1", List.of("a-1"));

        verify(allergenRepository).findById("a-1");
    }

    @Test
    void listAllergens_returnsCopy() {
        existing.addAllergen(new Allergen("a-1", "G"));
        when(userRepository.findById("u-1")).thenReturn(Optional.of(existing));

        List<Allergen> list = service.listAllergens("u-1");
        assertEquals(1, list.size());
    }

    @Test
    void updateUserRole() {
        when(userRepository.findById("u-1")).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User out = service.updateUserRole("u-1", Role.ROLE_OWNER);
        assertEquals(Role.ROLE_OWNER, out.getRole());
    }
}
