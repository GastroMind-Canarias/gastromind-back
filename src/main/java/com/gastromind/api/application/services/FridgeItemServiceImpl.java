package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.ports.in.IFridgeItemService;
import com.gastromind.api.domain.ports.out.FridgeItemRepository;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.domain.ports.out.ProductRepository;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ItemStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
/**
 * Servicio de aplicación para gestionar los productos almacenados en nevera.
 */
public class FridgeItemServiceImpl implements IFridgeItemService {

    private final FridgeItemRepository repository;
    private final ProductRepository productRepository;
    private final FridgeRepository fridgeRepository;
    /**
     * Crea el servicio con sus repositorios de inventario, producto y nevera.
     * @param repository repositorio de ítems de nevera
     * @param productRepository repositorio de productos
     * @param fridgeRepository repositorio de neveras
     */

    public FridgeItemServiceImpl(FridgeItemRepository repository, ProductRepository productRepository,
            FridgeRepository fridgeRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.fridgeRepository = fridgeRepository;
    }
    /**
     * Devuelve todos los ítems de nevera registrados.
     * @return listado completo de ítems
     */

    @Override
    public List<FridgeItem> findAll() {
        return repository.findAll();
    }
    /**
     * Busca un ítem de nevera por su identificador.
     * @param id identificador del ítem
     * @return ítem encontrado
     * @throws NotFoundException si el ítem no existe
     */

    @Override
    public FridgeItem findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item de nevera no encontrado"));
    }
    /**
     * Lista los ítems asociados a una nevera concreta.
     * @param fridgeId identificador de la nevera
     * @return ítems de la nevera indicada
     */

    @Override
    public List<FridgeItem> findByFridgeId(String fridgeId) {
        return repository.findByFridgeId(fridgeId);
    }
    /**
     * Registra un nuevo ítem de nevera.
     * @param fridgeItem datos del ítem a registrar
     * @return ítem persistido
     */

    @Override
    @Transactional
    public FridgeItem create(FridgeItem fridgeItem) {
        return repository.save(fridgeItem);
    }
    /**
     * Añade a la nevera un ítem vinculado a un producto del catálogo.
     * @param fridgeId identificador de la nevera
     * @param productId identificador del producto de catálogo
     * @param quantity cantidad del producto
     * @param expirationDate fecha de caducidad, si se conoce
     * @param initialStatus estado inicial del ítem
     * @return ítem creado en inventario
     * @throws NotFoundException si la nevera o el producto no existen
     */

    @Override
    @Transactional
    public FridgeItem addProductToFridge(String fridgeId, String productId, BigDecimal quantity,
            LocalDate expirationDate, ItemStatus initialStatus) {
        fridgeRepository.findById(fridgeId).orElseThrow(() -> new NotFoundException("Nevera no encontrada"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        FridgeItem newItem = new FridgeItem();
        newItem.setFridgeId(fridgeId);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        newItem.setExpirationDate(expirationDate);
        newItem.setStatus(initialStatus != null ? initialStatus : ItemStatus.GOOD);

        return repository.save(newItem);
    }
    /**
     * Añade a la nevera un ítem libre identificado solo por etiqueta.
     * @param fridgeId identificador de la nevera
     * @param productLabel etiqueta del producto introducida por el usuario
     * @param quantity cantidad del producto
     * @param expirationDate fecha de caducidad, si se conoce
     * @param initialStatus estado inicial del ítem
     * @return ítem creado en inventario
     */

    @Override
    @Transactional
    public FridgeItem addLabeledItemToFridge(String fridgeId, String productLabel, BigDecimal quantity,
            LocalDate expirationDate, ItemStatus initialStatus) {
        if (productLabel == null || productLabel.isBlank()) {
            throw new IllegalArgumentException("La etiqueta del producto no puede estar vacia");
        }
        fridgeRepository.findById(fridgeId).orElseThrow(() -> new NotFoundException("Nevera no encontrada"));

        FridgeItem newItem = new FridgeItem();
        newItem.setFridgeId(fridgeId);
        newItem.setProduct(null);
        newItem.setProductLabel(productLabel.trim());
        newItem.setQuantity(quantity);
        newItem.setExpirationDate(expirationDate);
        newItem.setStatus(initialStatus != null ? initialStatus : ItemStatus.GOOD);

        return repository.save(newItem);
    }
    /**
     * Descuenta parcialmente la cantidad de un ítem de nevera.
     * @param itemId identificador del ítem
     * @param quantityToConsume cantidad a consumir
     * @return ítem actualizado o marcado como consumido cuando llega a cero
     */

    @Override
    @Transactional
    public FridgeItem consumePartially(String itemId, BigDecimal quantityToConsume) {
        FridgeItem item = findById(itemId);
        if (item.getQuantity().compareTo(quantityToConsume) < 0) {
            throw new IllegalArgumentException("Cantidad a consumir mayor que la disponible");
        }
        item.setQuantity(item.getQuantity().subtract(quantityToConsume));
        if (item.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            item.setStatus(ItemStatus.CONSUMED);
            repository.deleteById(itemId);
            return item;
        }
        return repository.save(item);
    }
    /**
     * Marca un ítem como consumido eliminándolo del inventario activo.
     * @param itemId identificador del ítem
     */

    @Override
    @Transactional
    public void markAsConsumed(String itemId) {
        delete(itemId);
    }
    /**
     * Recupera ítems que caducan antes del umbral indicado.
     * @param fridgeId identificador de la nevera
     * @param daysThreshold número de días de horizonte
     * @return ítems próximos a caducar
     */

    @Override
    public List<FridgeItem> getExpiringItems(String fridgeId, int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return repository.findExpiringItems(fridgeId, thresholdDate);
    }
    /**
     * Lista el inventario de una nevera filtrado por categoría.
     * @param fridgeId identificador de la nevera
     * @param categoryId identificador de categoría
     * @return ítems de la categoría indicada
     */

    @Override
    public List<FridgeItem> getInventoryByCategory(String fridgeId, String categoryId) {
        return repository.findByFridgeIdAndCategoryId(fridgeId, categoryId);
    }
    /**
     * Define un ítem de nevera existente.
     * @param id identificador del ítem
     * @param fridgeItem nuevos datos del ítem
     * @return ítem actualizado
     */

    @Override
    @Transactional
    public FridgeItem update(String id, FridgeItem fridgeItem) {
        this.findById(id);
        fridgeItem.setId(id);
        return repository.save(fridgeItem);
    }
    /**
     * Elimina un ítem de nevera por su identificador.
     * @param id identificador del ítem
     */

    @Override
    @Transactional
    public void delete(String id) {
        this.findById(id);
        repository.deleteById(id);
    }
}




