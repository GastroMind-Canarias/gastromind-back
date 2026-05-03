package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.ports.out.UserRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.UserMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
/**
 * Representa user dentro del dominio de la aplicacion.
 */
public class UserAdapter implements UserRepository {

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    UserMapper userMapper;
    /**
     * Registra un nuevo user.
     * @param user valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        return userMapper.toDomain(userJpaRepository.save(entity));
    }
    /**
     * Devuelve user por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<User> findById(String id) {
        return userJpaRepository.findById(id).map(userMapper::toDomain);
    }
    /**
     * Devuelve user por name.
     * @param name el nombre
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<User> findByName(String name) {
        return userJpaRepository.findByName(name).map(userMapper::toDomain);
    }
    /**
     * Devuelve user por email.
     * @param email el correo electronico
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userMapper::toDomain);
    }
    /**
     * Devuelve user por household id.
     * @param householdId el identificador del hogar
     * @return lista actual.
     */

    @Override
    public List<User> findByHouseholdId(String householdId) {
        return userMapper.toDomainList(userJpaRepository.findByHouseholdId(householdId));
    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
        userJpaRepository.deleteById(id);
    }
    /**
     * Lista todos los user.
     * @return lista actual.
     */

    @Override
    public List<User> findAll() {
        List<UserEntity> userEntities = userJpaRepository.findAll();
        return userMapper.toDomainList(userEntities);
    }
}




