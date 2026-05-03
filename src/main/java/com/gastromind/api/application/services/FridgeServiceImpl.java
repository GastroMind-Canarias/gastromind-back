package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.ports.in.IFridgeService;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Servicio de aplicacion para gestionar neveras.
 */
public class FridgeServiceImpl implements IFridgeService {

    private final FridgeRepository repository;
    /**
     * Crea el servicio con el repositorio de neveras.
     * @param repository repositorio de persistencia de neveras
     */


    public FridgeServiceImpl(FridgeRepository repository) {
        this.repository = repository;
    }
    /**
     * Devuelve todas las neveras registradas.
     * @return listado completo de neveras
     */

    @Override
    public List<Fridge> findAll() {
        return repository.findAll();
    }
    /**
     * Busca una nevera por su identificador.
     * @param id identificador de la nevera
     * @return nevera encontrada
     * @throws NotFoundException si no existe una nevera con ese id
     */

    @Override
    public Fridge findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Nevera no encontrada"));
    }
    /**
     * Crea una nueva nevera.
     * @param fridge datos de la nevera a crear
     * @return nevera persistida
     */

    @Override
    public Fridge create(Fridge fridge) {
        return repository.save(fridge);
    }
    /**
     * Define una nevera existente.
     * @param id identificador de la nevera a actualizar
     * @param fridge nuevos datos de la nevera
     * @return nevera actualizada
     * @throws NotFoundException si no existe una nevera con ese id
     */

    @Override
    public Fridge update(String id, Fridge fridge) {
        findById(id);
        fridge.setId(id);
        return repository.save(fridge);
    }
    /**
     * Elimina una nevera por su identificador.
     * @param id identificador de la nevera a eliminar
     * @throws NotFoundException si no existe una nevera con ese id
     */

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}




