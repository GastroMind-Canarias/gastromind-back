package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.ports.in.IUnitService;
import com.gastromind.api.domain.ports.out.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Servicio de aplicación para gestionar unidades de medida.
 */
public class UnitServiceImpl implements IUnitService {

    private final UnitRepository repository;
    /**
     * Crea el servicio con el repositorio de unidades.
     * @param repository repositorio de persistencia de unidades
     */


    public UnitServiceImpl(UnitRepository repository) {
        this.repository = repository;
    }
    /**
     * Devuelve todas las unidades de medida registradas.
     * @return listado completo de unidades
     */

    @Override
    public List<Unit> findAll() {
        return repository.findAll();
    }
    /**
     * Busca una unidad de medida por su identificador.
     * @param id identificador de la unidad
     * @return unidad encontrada
     * @throws NotFoundException si no existe una unidad con ese id
     */

    @Override
    public Unit findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Unidad de Medida no encontrada"));
    }
    /**
     * Crea una nueva unidad de medida.
     * @param unit datos de la unidad a crear
     * @return unidad persistida
     */

    @Override
    public Unit create(Unit unit) {
        return repository.save(unit);
    }
    /**
     * Define una unidad de medida existente.
     * @param id identificador de la unidad a actualizar
     * @param unit nuevos datos de la unidad
     * @return unidad actualizada
     * @throws NotFoundException si no existe una unidad con ese id
     */

    @Override
    public Unit update(String id, Unit unit) {
        findById(id);
        unit.setId(id);
        return repository.save(unit);
    }
    /**
     * Elimina una unidad de medida por su identificador.
     * @param id identificador de la unidad a eliminar
     * @throws NotFoundException si no existe una unidad con ese id
     */

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}




