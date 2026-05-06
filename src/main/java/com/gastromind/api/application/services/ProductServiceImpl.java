package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.ports.in.IProductService;
import com.gastromind.api.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
/**
 * Servicio de aplicacion para gestionar productos del catalogo.
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

    @Override
    public List<Product> createBatch(List<String> names) {
        if (names == null || names.isEmpty()) {
            throw new IllegalArgumentException("Debes indicar al menos un nombre de producto");
        }

        Map<String, String> uniqueNamesByLowercase = new LinkedHashMap<>();
        for (String name : names) {
            if (name == null) {
                continue;
            }
            String trimmed = name.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            uniqueNamesByLowercase.putIfAbsent(trimmed.toLowerCase(Locale.ROOT), trimmed);
        }
        if (uniqueNamesByLowercase.isEmpty()) {
            throw new IllegalArgumentException("Debes indicar al menos un nombre de producto");
        }

        List<Product> result = new ArrayList<>();
        for (String normalizedKey : uniqueNamesByLowercase.keySet()) {
            String candidateName = uniqueNamesByLowercase.get(normalizedKey);
            Product product = repository.findFirstByNameIgnoreCase(candidateName).orElseGet(() -> {
                Product newProduct = new Product();
                newProduct.setName(candidateName);
                newProduct.setIs_essential(false);
                return repository.save(newProduct);
            });
            result.add(product);
        }
        return result;
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




