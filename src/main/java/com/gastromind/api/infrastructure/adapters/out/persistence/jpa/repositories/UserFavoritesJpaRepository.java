package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserFavoritesEntity;

import org.springframework.stereotype.Repository;

@Repository
public interface UserFavoritesJpaRepository extends JpaRepository<UserFavoritesEntity,String> {
    
}
