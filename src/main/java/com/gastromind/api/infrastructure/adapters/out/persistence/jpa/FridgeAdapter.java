package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.FridgeEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.FridgeMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.FridgeJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
/**
 * Representa fridge dentro del dominio de la aplicacion.
 */
public class FridgeAdapter implements FridgeRepository {

    @Autowired
    FridgeJpaRepository fridgeJpaRepository;

    @Autowired
    FridgeMapper fridgeMapper;
    /**
     * Registra un nuevo fridge.
     * @param fridge la nevera
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Fridge save(Fridge fridge) {
        FridgeEntity entity = fridgeMapper.toEntity(fridge);
        return fridgeMapper.toDomain(fridgeJpaRepository.save(entity));
    }
    /**
     * Devuelve fridge por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<Fridge> findById(String id) {
        return fridgeJpaRepository.findById(id).map(fridgeMapper::toDomain);
    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
        fridgeJpaRepository.deleteById(id);
    }
    /**
     * Lista todos los fridge.
     * @return lista actual.
     */

    @Override
    public List<Fridge> findAll() {
        List<FridgeEntity> fridgeEntities = fridgeJpaRepository.findAll();
        return fridgeMapper.toDomainList(fridgeEntities);
    }
    /**
     * Devuelve fridge por household id.
     * @param householdId el identificador del hogar
     * @return lista actual.
     */

    @Override
    public List<Fridge> findByHouseholdId(String householdId) {
        return fridgeMapper.toDomainList(fridgeJpaRepository.findByHousehold_Id(householdId));
    }
    /**
     * Realiza find first by household id.
     * @param householdId el identificador del hogar
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<Fridge> findFirstByHouseholdId(String householdId) {
        return fridgeJpaRepository.findFirstByHousehold_IdOrderByIdAsc(householdId).map(fridgeMapper::toDomain);
    }

}




