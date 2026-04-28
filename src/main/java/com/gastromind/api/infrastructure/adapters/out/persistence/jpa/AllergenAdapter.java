package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.ports.out.AllergenRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.AllergenEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.AllergenMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.AllergenJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
/**
 * Representa allergen dentro del dominio de la aplicacion.
 */
public class AllergenAdapter implements AllergenRepository {

    @Autowired
    AllergenJpaRepository allergenJpaRepository;

    @Autowired
    AllergenMapper allergenMapper;
    /**
     * Registra un nuevo allergen.
     * @param allergen el alergeno
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Allergen save(Allergen allergen) {
        AllergenEntity entity = allergenMapper.toEntity(allergen);
        return allergenMapper.toDomain(allergenJpaRepository.save(entity));

    }
    /**
     * Devuelve allergen por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<Allergen> findById(String id) {
        return allergenJpaRepository.findById(id).map(allergenMapper::toDomain);

    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
        allergenJpaRepository.deleteById(id);
    }
    /**
     * Lista todos los allergen.
     * @return lista actual.
     */

    @Override
    public List<Allergen> findAll() {
        List<AllergenEntity> allergenEntities = allergenJpaRepository.findAll();
        return allergenMapper.toDomainList(allergenEntities);
    }

}




