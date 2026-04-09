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
public class FridgeItemServiceImpl implements IFridgeItemService {

    private final FridgeItemRepository repository;
    private final ProductRepository productRepository;
    private final FridgeRepository fridgeRepository;

    public FridgeItemServiceImpl(FridgeItemRepository repository, ProductRepository productRepository,
            FridgeRepository fridgeRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.fridgeRepository = fridgeRepository;
    }

    @Override
    public List<FridgeItem> findAll() {
        return repository.findAll();
    }

    @Override
    public FridgeItem findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item de nevera no encontrado"));
    }

    @Override
    public List<FridgeItem> findByFridgeId(String fridgeId) {
        return repository.findByFridgeId(fridgeId);
    }

    @Override
    @Transactional
    public FridgeItem create(FridgeItem fridgeItem) {
        return repository.save(fridgeItem);
    }

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

    @Override
    @Transactional
    public void markAsConsumed(String itemId) {
        delete(itemId);
    }

    @Override
    public List<FridgeItem> getExpiringItems(String fridgeId, int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return repository.findExpiringItems(fridgeId, thresholdDate);
    }

    @Override
    public List<FridgeItem> getInventoryByCategory(String fridgeId, String categoryId) {
        return repository.findByFridgeIdAndCategoryId(fridgeId, categoryId);
    }

    @Override
    @Transactional
    public FridgeItem update(String id, FridgeItem fridgeItem) {
        this.findById(id);
        fridgeItem.setId(id);
        return repository.save(fridgeItem);
    }

    @Override
    @Transactional
    public void delete(String id) {
        this.findById(id);
        repository.deleteById(id);
    }
}