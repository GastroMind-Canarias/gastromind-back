package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.domain.ports.in.IStoreService;
import com.gastromind.api.domain.ports.out.StoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Servicio de aplicacion para gestionar tiendas.
 */
public class StoreServiceImpl implements IStoreService {

    private final StoreRepository repository;
    /**
     * Crea el servicio con el repositorio de tiendas.
     * @param repository repositorio de persistencia de tiendas
     */


    public StoreServiceImpl(StoreRepository repository) {
        this.repository = repository;
    }
    /**
     * Devuelve todas las tiendas registradas.
     * @return listado completo de tiendas
     */

    @Override
    public List<Store> findAll() {
        return repository.findAll();
    }
    /**
     * Busca una tienda por su identificador.
     * @param id identificador de la tienda
     * @return tienda encontrada
     * @throws NotFoundException si no existe una tienda con ese id
     */

    @Override
    public Store findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Tienda no encontrada"));
    }
    /**
     * Crea una nueva tienda.
     * @param store datos de la tienda a crear
     * @return tienda persistida
     */

    @Override
    public Store create(Store store) {
        return repository.save(store);
    }
    /**
     * Define una tienda existente.
     * @param id identificador de la tienda a actualizar
     * @param store nuevos datos de la tienda
     * @return tienda actualizada
     * @throws NotFoundException si no existe una tienda con ese id
     */

    @Override
    public Store update(String id, Store store) {
        findById(id);
        store.setId(id);
        return repository.save(store);
    }
    /**
     * Elimina una tienda por su identificador.
     * @param id identificador de la tienda a eliminar
     * @throws NotFoundException si no existe una tienda con ese id
     */

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}




