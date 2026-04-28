package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.ports.in.IProductService;
import com.gastromind.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Servicio de aplicación para gestionar productos del catálogo.
 */
public class ProductServiceImpl implements IProductService {

    private final ProductRepository repository;
    /**
     * Crea el servicio con el repositorio de productos.
     * @param repository repositorio de persistencia de productos
     */


    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }
    /**
     * Devuelve todos los productos registrados.
     * @return listado completo de productos
     */

    @Override
    public List<Product> findAll() {
        return repository.findAll();
    }
    /**
     * Busca un producto por su identificador.
     * @param id identificador del producto
     * @return producto encontrado
     * @throws NotFoundException si no existe un producto con ese id
     */

    @Override
    public Product findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Producto no encontrado"));
    }
    /**
     * Registra un nuevo producto.
     * @param product datos del producto a crear
     * @return producto persistido
     */

    @Override
    public Product create(Product product) {
        return repository.save(product);
    }
    /**
     * Define un producto existente.
     * @param id identificador del producto a actualizar
     * @param product nuevos datos del producto
     * @return producto actualizado
     * @throws NotFoundException si no existe un producto con ese id
     */

    @Override
    public Product update(String id, Product product) {
        findById(id);
        product.setId(id);
        return repository.save(product);
    }
    /**
     * Elimina un producto por su identificador.
     * @param id identificador del producto a eliminar
     * @throws NotFoundException si no existe un producto con ese id
     */

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}




