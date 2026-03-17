package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.AllergenEntity;

@Repository
public interface AllergenJpaRepository extends JpaRepository<AllergenEntity, String> {

}
