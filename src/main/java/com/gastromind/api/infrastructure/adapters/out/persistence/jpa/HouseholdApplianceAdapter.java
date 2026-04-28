package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.HouseholdAppliance;
import com.gastromind.api.domain.ports.out.HouseholdApplianceRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdApplianceEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.HouseholdApplianceMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.HouseholdApplianceJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
/**
 * Representa household appliance dentro del dominio de la aplicacion.
 */
public class HouseholdApplianceAdapter implements HouseholdApplianceRepository {

    @Autowired
    private HouseholdApplianceJpaRepository jpaRepository;

    @Autowired
    private HouseholdApplianceMapper mapper;
    /**
     * Registra un nuevo household appliance.
     * @param appliance valor a utilizar.
     * @return resultado de la operacion solicitada.
     */

    @Override
    public HouseholdAppliance save(HouseholdAppliance appliance) {
        HouseholdApplianceEntity entity = mapper.toEntity(appliance);
        return mapper.toDomain(jpaRepository.save(entity));
    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
    /**
     * Realiza delete all by household id.
     * @param householdId el identificador del hogar
     */

    @Override
    public void deleteAllByHouseholdId(String householdId) {
        jpaRepository.deleteAllByHousehold_Id(householdId);
    }
    /**
     * Devuelve household appliance por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<HouseholdAppliance> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    /**
     * Devuelve household appliance por household id.
     * @param householdId el identificador del hogar
     * @return lista actual.
     */

    @Override
    public List<HouseholdAppliance> findByHouseholdId(String householdId) {
        return mapper.toDomainList(jpaRepository.findByHousehold_Id(householdId));
    }
}




