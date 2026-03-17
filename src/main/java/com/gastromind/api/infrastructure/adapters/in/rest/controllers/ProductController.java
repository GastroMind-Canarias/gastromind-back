package com.gastromind.api.infrastructure.adapters.in.rest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Producto", description = "Gestión del catálogo de productos disponibles en el sistema.")
public class ProductController {

    @Autowired
    private ProductServiceImpl productServiceImpl;

    @Autowired
    private ProductRestMapper productMapper;

    @Operation(summary = "Obtener todos los productos", description = "Devuelve una lista completa de todos los productos registrados.")
    @ApiStandardDoc
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        List<Product> products = productServiceImpl.findAll();
        return ResponseEntity.ok(productMapper.toResponseList(products));
    }

    @Operation(summary = "Buscar producto por ID", description = "Devuelve un único producto basándose en su identificador único.")
    @ApiStandardDoc
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(
            @Parameter(description = "ID del producto a buscar", example = "1") @PathVariable String id) {
        Product product = productServiceImpl.findById(id);
        return ResponseEntity.ok(productMapper.toResponse(product));
    }

    @Operation(summary = "Crear nuevo producto", description = "Registra un nuevo producto en el sistema.")
    @ApiPostDoc
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        Product productDomain = productMapper.toDomain(request);
        Product savedProduct = productServiceImpl.create(productDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toResponse(savedProduct));
    }

    @Operation(summary = "Actualizar producto", description = "Modifica los datos de un producto existente.")
    @ApiStandardDoc
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable String id, @Valid @RequestBody ProductRequest request) {
        Product productDomain = productMapper.toDomain(request);
        Product updatedProduct = productServiceImpl.update(id, productDomain);
        return ResponseEntity.ok(productMapper.toResponse(updatedProduct));
    }

    @Operation(summary = "Eliminar producto", description = "Borra físicamente un producto de la base de datos.")
    @ApiStandardDoc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }
}