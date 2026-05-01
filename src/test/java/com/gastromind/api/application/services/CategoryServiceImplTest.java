package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Category;
import com.gastromind.api.domain.ports.out.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category existing;

    @BeforeEach
    void setUp() {
        existing = new Category("cat-1", "Lácteos");
    }

    @Test
    void findAll_delegatesToRepository() {
        when(repository.findAll()).thenReturn(List.of(existing));

        assertEquals(List.of(existing), categoryService.findAll());
        verify(repository).findAll();
    }

    @Test
    void findById_returnsCategoryWhenPresent() {
        when(repository.findById("cat-1")).thenReturn(Optional.of(existing));

        assertEquals(existing, categoryService.findById("cat-1"));
    }

    @Test
    void findById_throwsWhenMissing() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> categoryService.findById("missing"));
        assertEquals("Categoria no encontrada", ex.getMessage());
    }

    @Test
    void create_savesAndReturns() {
        Category input = new Category(null, "Bebidas");
        Category saved = new Category("new-id", "Bebidas");
        when(repository.save(input)).thenReturn(saved);

        assertEquals(saved, categoryService.create(input));
        verify(repository).save(input);
    }

    @Test
    void update_loadsExisting_setsIdAndSaves() {
        when(repository.findById("cat-1")).thenReturn(Optional.of(existing));
        Category patch = new Category(null, "Lácteos actualizado");
        Category saved = new Category("cat-1", "Lácteos actualizado");
        when(repository.save(any(Category.class))).thenReturn(saved);

        Category result = categoryService.update("cat-1", patch);

        assertEquals(saved, result);
        verify(repository).findById("cat-1");
        verify(repository).save(any(Category.class));
    }

    @Test
    void update_throwsWhenIdNotFound() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.update("missing", new Category()));
        verify(repository).findById("missing");
    }

    @Test
    void delete_verifiesExistsThenDeletes() {
        when(repository.findById("cat-1")).thenReturn(Optional.of(existing));

        categoryService.delete("cat-1");

        verify(repository).findById("cat-1");
        verify(repository).deleteById(eq("cat-1"));
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.delete("missing"));
        verify(repository).findById("missing");
    }
}
