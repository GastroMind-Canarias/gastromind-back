package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.ports.out.HouseHoldRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.HouseholdMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.HouseHoldJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
/**
 * Representa house hold dentro del dominio de la aplicacion.
 */
public class HouseHoldAdapter implements HouseHoldRepository {

    @Autowired
    HouseHoldJpaRepository holdJpaRepository;

    @Autowired
    HouseholdMapper householdMapper;
    /**
     * Registra un nuevo house hold.
     * @param houseHold valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Override
    public HouseHold save(HouseHold houseHold) {
        HouseholdEntity entity = householdMapper.toEntity(houseHold);
        return householdMapper.toDomain(holdJpaRepository.save(entity));
    }
    /**
     * Realiza exists by id.
     * @param id el identificador del recurso
     * @return true si cumple la condicion; false en caso contrario.
     */

    @Override
    public boolean existsById(String id) {
        return holdJpaRepository.existsById(id);
    }
    /**
     * Devuelve house hold por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<HouseHold> findById(String id) {
        return holdJpaRepository.findById(id).map(householdMapper::toDomain);
    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
        holdJpaRepository.deleteById(id);
    }
    /**
     * Lista todos los house hold.
     * @return lista actual.
     */

    @Override
    public List<HouseHold> findAll() {
        List<HouseholdEntity> houseHoldEntities = holdJpaRepository.findAll();
        return householdMapper.toDomainList(houseHoldEntities);
    }

}




