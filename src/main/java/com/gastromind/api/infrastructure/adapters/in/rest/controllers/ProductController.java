package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import com.gastromind.api.application.services.ProductServiceImpl;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiPostDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.doc.ApiStandardDoc;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.product.ProductRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.product.ProductResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.mappers.ProductRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Producto", description = "Gestion del catalogo de productos disponibles.")
/**
 * Controlador REST para gestionar productos.
 */
public class ProductController {

    @Autowired
    private ProductServiceImpl productServiceImpl;

    @Autowired
    private ProductRestMapper productMapper;
    /**
     * Lista todos los productos.
     *
     * @return coleccion de productos
     */

    @Operation(summary = "Obtener todos los productos", description = "Devuelve una lista completa de todos los productos registrados.")
    @ApiStandardDoc
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductResponse>> getAll() {
        List<Product> products = productServiceImpl.findAll();
        return ResponseEntity.ok(productMapper.toResponseList(products));
    }
    /**
     * Recupera un producto por ID.
     *
     * @param id identificador del producto
     * @return producto encontrado
     */

    @Operation(summary = "Buscar producto por ID", description = "Devuelve un producto concreto a partir de su identificador.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductResponse> getById(
            @Parameter(description = "ID del producto a buscar", example = "1") @PathVariable String id) {
        Product product = productServiceImpl.findById(id);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }
    /**
     * Crea un producto.
     *
     * @param request datos de alta
     * @return producto creado
     */

    @Operation(summary = "Crear nuevo producto", description = "Registra un nuevo producto en el sistema.")
    @ApiPostDoc
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        Product productDomain = productMapper.toDomain(request);
        Product savedProduct = productServiceImpl.create(productDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toResponse(savedProduct));
    }
    /**
     * Define un producto existente.
     *
     * @param id identificador del producto
     * @param request datos actualizados
     * @return producto actualizado
     */

    @Operation(summary = "Actualizar producto", description = "Modifica los datos de un producto existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> update(@PathVariable String id, @Valid @RequestBody ProductRequest request) {
        Product productDomain = productMapper.toDomain(request);
        Product updatedProduct = productServiceImpl.update(id, productDomain);
        return ResponseEntity.ok(productMapper.toResponse(updatedProduct));
    }
    /**
     * Elimina un producto.
     *
     * @param id identificador del producto
     * @return respuesta sin contenido
     */

    @Operation(summary = "Eliminar producto", description = "Elimina un producto de forma permanente.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}




