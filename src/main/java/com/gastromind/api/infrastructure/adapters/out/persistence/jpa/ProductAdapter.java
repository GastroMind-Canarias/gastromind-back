package com.gastromind.api.infrastructure.adapters.out.persistence.jpa;

import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.ports.out.ProductRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.ProductEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers.ProductMapper;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories.ProductJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
/**
 * Representa product dentro del dominio de la aplicacion.
 */
public class ProductAdapter implements ProductRepository {

    @Autowired
    ProductJpaRepository productJpaRepository;

    @Autowired
    ProductMapper productMapper;
    /**
     * Registra un nuevo product.
     * @param product el producto
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Product save(Product product) {
        ProductEntity entity = productMapper.toEntity(product);
        return productMapper.toDomain(productJpaRepository.save(entity));
    }
    /**
     * Devuelve product por id.
     * @param id el identificador del recurso
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<Product> findById(String id) {
       return productJpaRepository.findById(id).map(productMapper::toDomain);
    }
    /**
     * Realiza delete by id.
     * @param id el identificador del recurso
     */

    @Override
    public void deleteById(String id) {
        productJpaRepository.deleteById(id);
    }
    /**
     * Lista todos los product.
     * @return lista actual.
     */

    @Override
    public List<Product> findAll() {
        List<ProductEntity> productEntities = productJpaRepository.findAll();
        return productMapper.toDomainList(productEntities);
    }
    /**
     * Realiza find first by name ignore case.
     * @param name el nombre
     * @return resultado de la operacion solicitada.
     */

    @Override
    public Optional<Product> findFirstByNameIgnoreCase(String name) {
        return productJpaRepository.findFirstByNameIgnoreCase(name).map(productMapper::toDomain);
    }

}




