package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.FridgeItem;
import com.gastromind.api.domain.models.FridgeItemConsumeLine;
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
import java.util.ArrayList;
import java.util.List;

@Service
/**
 * Servicio de aplicacion para gestionar los productos almacenados en nevera.
 */
public class FridgeItemServiceImpl implements IFridgeItemService {

    private final FridgeItemRepository repository;
    private final ProductRepository productRepository;
    private final FridgeRepository fridgeRepository;
    /**
     * Crea el servicio con sus repositorios de inventario, producto y nevera.
     * @param repository repositorio de items de nevera
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
     * Devuelve todos los items de nevera registrados.
     * @return listado completo de items
     */

    @Override
    public List<FridgeItem> findAll() {
        return repository.findAll();
    }
    /**
     * Busca un item de nevera por su identificador.
     * @param id identificador del item
     * @return item encontrado
     * @throws NotFoundException si el item no existe
     */

    @Override
    public FridgeItem findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item de nevera no encontrado"));
    }
    /**
     * Lista los items asociados a una nevera concreta.
     * @param fridgeId identificador de la nevera
     * @return items de la nevera indicada
     */

    @Override
    public List<FridgeItem> findByFridgeId(String fridgeId) {
        return repository.findByFridgeId(fridgeId);
    }
    /**
     * Registra un nuevo item de nevera.
     * @param fridgeItem datos del item a registrar
     * @return item persistido
     */

    @Override
    @Transactional
    public FridgeItem create(FridgeItem fridgeItem) {
        return repository.save(fridgeItem);
    }
    /**
     * Anade a la nevera un item vinculado a un producto del catalogo.
     * @param fridgeId identificador de la nevera
     * @param productId identificador del producto de catalogo
     * @param quantity cantidad del producto
     * @param expirationDate fecha de caducidad, si se conoce
     * @param initialStatus estado inicial del item
     * @return item creado en inventario
     * @throws NotFoundException si la nevera o el producto no existen
     */

    @Override
    @Transactional
    public FridgeItem addProductToFridge(String fridgeId, String productId, BigDecimal quantity,
            LocalDate expirationDate, ItemStatus initialStatus) {
        fridgeRepository.findById(fridgeId).orElseThrow(() -> new NotFoundException("Nevera no encontrada"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
        List<FridgeItem> existingItems = repository.findByFridgeId(fridgeId);

        for (FridgeItem existing : existingItems) {
            if (existing.getProduct() == null || existing.getProduct().getId() == null) {
                continue;
            }
            if (!existing.getProduct().getId().equals(product.getId())) {
                continue;
            }
            existing.setQuantity(existing.getQuantity().add(quantity));
            if (existing.getExpirationDate() == null) {
                existing.setExpirationDate(expirationDate);
            }
            if (existing.getStatus() == null) {
                existing.setStatus(initialStatus != null ? initialStatus : ItemStatus.GOOD);
            }
            return repository.save(existing);
        }

        FridgeItem newItem = new FridgeItem();
        newItem.setFridgeId(fridgeId);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        newItem.setExpirationDate(expirationDate);
        newItem.setStatus(initialStatus != null ? initialStatus : ItemStatus.GOOD);

        return repository.save(newItem);
    }
    /**
     * Anade a la nevera un item libre identificado solo por etiqueta.
     * @param fridgeId identificador de la nevera
     * @param productLabel etiqueta del producto introducida por el usuario
     * @param quantity cantidad del producto
     * @param expirationDate fecha de caducidad, si se conoce
     * @param initialStatus estado inicial del item
     * @return item creado en inventario
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
     * Descuenta parcialmente la cantidad de un item de nevera.
     * @param itemId identificador del item
     * @param quantityToConsume cantidad a consumir
     * @return item actualizado o marcado como consumido cuando llega a cero
     */

    @Override
    @Transactional
    public FridgeItem consumePartially(String itemId, BigDecimal quantityToConsume) {
        return applyConsumePartially(itemId, quantityToConsume);
    }

    /**
     * Varios descuentos en una sola transaccion (si una linea falla, ninguna se aplica).
     */
    @Override
    @Transactional
    public List<FridgeItem> consumePartiallyBatch(List<FridgeItemConsumeLine> lines) {
        List<FridgeItem> updated = new ArrayList<>(lines.size());
        for (FridgeItemConsumeLine line : lines) {
            updated.add(applyConsumePartially(line.itemId(), line.quantity()));
        }
        return updated;
    }

    private FridgeItem applyConsumePartially(String itemId, BigDecimal quantityToConsume) {
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
     * Marca un item como consumido eliminandolo del inventario activo.
     * @param itemId identificador del item
     */

    @Override
    @Transactional
    public void markAsConsumed(String itemId) {
        delete(itemId);
    }
    /**
     * Recupera items que caducan antes del umbral indicado.
     * @param fridgeId identificador de la nevera
     * @param daysThreshold numero de dias de horizonte
     * @return items proximos a caducar
     */

    @Override
    public List<FridgeItem> getExpiringItems(String fridgeId, int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return repository.findExpiringItems(fridgeId, thresholdDate);
    }
    /**
     * Lista el inventario de una nevera filtrado por categoria.
     * @param fridgeId identificador de la nevera
     * @param categoryId identificador de categoria
     * @return items de la categoria indicada
     */

    @Override
    public List<FridgeItem> getInventoryByCategory(String fridgeId, String categoryId) {
        return repository.findByFridgeIdAndCategoryId(fridgeId, categoryId);
    }
    /**
     * Define un item de nevera existente.
     * @param id identificador del item
     * @param fridgeItem nuevos datos del item
     * @return item actualizado
     */

    @Override
    @Transactional
    public FridgeItem update(String id, FridgeItem fridgeItem) {
        this.findById(id);
        fridgeItem.setId(id);
        return repository.save(fridgeItem);
    }
    /**
     * Elimina un item de nevera por su identificador.
     * @param id identificador del item
     */

    @Override
    @Transactional
    public void delete(String id) {
        this.findById(id);
        repository.deleteById(id);
    }
}




