package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.ports.out.UserRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UserJpaRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.UserMapper;

@Component
public class UserAdapter implements UserRepository {

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    UserMapper userMapper;

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

}
