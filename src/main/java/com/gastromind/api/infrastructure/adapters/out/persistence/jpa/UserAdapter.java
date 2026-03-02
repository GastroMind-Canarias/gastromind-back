package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.ports.out.UserRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.AllergenEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.AllergenMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.UserMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.AllergenJpaRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UserJpaRepository;

@Component
public class UserAdapter implements UserRepository {

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    AllergenJpaRepository allergenJpaRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    AllergenMapper allergenMapper;

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        return userMapper.toDomain(userJpaRepository.save(entity));
    }

    @Override
    public Optional<User> findById(String id) {
        return userJpaRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByName(String name) {
        return userJpaRepository.findByName(name).map(userMapper::toDomain);
    }

    @Override
    public List<User> findByHouseholdId(String householdId) {
        return userMapper.toDomainList(userJpaRepository.findByHouseholdId(householdId));
    }

    @Override
    public void deleteById(String id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public List<User> findAll() {
        List<UserEntity> userEntities = userJpaRepository.findAll();
        return userMapper.toDomainList(userEntities);
    }

    @Override
    public void addAllergenToUser(String userId, String allergenId) {
        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));
        AllergenEntity allergen = allergenJpaRepository.findById(allergenId)
                .orElseThrow(() -> new RuntimeException("Alérgeno no encontrado: " + allergenId));

        Set<AllergenEntity> allergens = user.getAllergens();
        if (allergens == null) {
            allergens = new HashSet<>();
            user.setAllergens(allergens);
        }
        allergens.add(allergen);
        userJpaRepository.save(user);
    }

    @Override
    public void removeAllergenFromUser(String userId, String allergenId) {
        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));

        Set<AllergenEntity> allergens = user.getAllergens();
        if (allergens != null) {
            allergens.removeIf(a -> a.getId().equals(allergenId));
            userJpaRepository.save(user);
        }
    }

    @Override
    public List<Allergen> findAllergensByUserId(String userId) {
        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));

        Set<AllergenEntity> allergens = user.getAllergens();
        if (allergens == null)
            return new ArrayList<>();
        return allergenMapper.toDomainList(new ArrayList<>(allergens));
    }
}
