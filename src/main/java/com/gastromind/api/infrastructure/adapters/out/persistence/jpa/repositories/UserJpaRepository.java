package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByName(String name);

    List<UserEntity> findByHouseholdId(String householdId);
}
