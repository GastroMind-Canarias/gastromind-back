package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.ports.out.UnitRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UnitEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.UnitMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.UnitJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
/**
 * Representa unit dentro del dominio de la aplicacion.
 */
public class UnitAdapter implements UnitRepository {

    @Autowired
    UnitJpaRepository unitJpaRepository;

    @Autowired
    UnitMapper unitMapper;
    /**
     * Registra un nuevo unit.
     * @param unit la unidad
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Unit save(Unit unit) {
        UnitEntity entity = unitMapper.toEntity(unit);
        return unitMapper.toDomain(unitJpaRepository.save(entity));
    }
    /**
     * Devuelve unit por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<Unit> findById(String id) {
        return unitJpaRepository.findById(id).map(unitMapper::toDomain);
    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
        unitJpaRepository.deleteById(id);
    }
    /**
     * Lista todos los unit.
     * @return lista actual.
     */

    @Override
    public List<Unit> findAll() {
        List<UnitEntity> unitEntities = unitJpaRepository.findAll();
        return unitMapper.toDomainList(unitEntities);
    }
    /**
     * Realiza find first by name ignore case.
     * @param name el nombre
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<Unit> findFirstByNameIgnoreCase(String name) {
        return unitJpaRepository.findFirstByNameIgnoreCase(name).map(unitMapper::toDomain);
    }
}




