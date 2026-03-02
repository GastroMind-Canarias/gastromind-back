package com.gastromind.api.application.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.ports.in.IUserService;
import com.gastromind.api.domain.ports.out.UserRepository;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User findById(String id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @Override
    public User create(User user) {
        return repository.save(user);
    }

    @Override
    public User update(String id, User user) {
        findById(id);
        user.setId(id);
        return repository.save(user);
    }

    @Override
    public User updateProfile(String id, String name, String email) {
        User user = findById(id);
        if (name != null)
            user.setName(name);
        if (email != null)
            user.setEmail(email);
        return repository.save(user);
    }

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }

    /** RegistrarNuevoAlergenoDeUsuario */
    @Override
    @Transactional
    public void addAllergen(String userId, String allergenId) {
        // Verificar que el usuario existe antes de añadir el alérgeno
        findById(userId);
        repository.addAllergenToUser(userId, allergenId);
    }

    /** EliminarAlergenoDeUsuario */
    @Override
    @Transactional
    public void removeAllergen(String userId, String allergenId) {
        findById(userId);
        repository.removeAllergenFromUser(userId, allergenId);
    }

    /** ListarAlergenosDeUsuario */
    @Override
    public List<Allergen> listAllergens(String userId) {
        findById(userId);
        return repository.findAllergensByUserId(userId);
    }
}