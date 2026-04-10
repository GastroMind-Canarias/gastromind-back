package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.UsualPurchase;
import com.gastromind.api.domain.ports.in.IUsualPurchaseService;
import com.gastromind.api.domain.ports.out.UsualPurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsualPurchaseServiceImpl implements IUsualPurchaseService {

    private final UsualPurchaseRepository repository;


    public UsualPurchaseServiceImpl(UsualPurchaseRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UsualPurchase> findAll() {
        return repository.findAll();
    }

    @Override
    public List<UsualPurchase> findAllByUserId(String userId) {
        return repository.findAllByUserId(userId);
    }

    @Override
    public UsualPurchase findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Producto más comprado no encontrado"));
    }

    @Override
    public UsualPurchase findByIdForUser(String id, String userId) {
        UsualPurchase up = findById(id);
        requireUsualPurchaseOwner(up, userId);
        return up;
    }

    private static void requireUsualPurchaseOwner(UsualPurchase usualPurchase, String userId) {
        if (usualPurchase.getUser_id() == null || usualPurchase.getUser_id().getId() == null
                || !usualPurchase.getUser_id().getId().equals(userId)) {
            throw new ForbiddenException("No tiene acceso a este registro de compra habitual");
        }
    }

    @Override
    public UsualPurchase create(UsualPurchase usualPurchase) {
        requireUserAndProductIds(usualPurchase);
        String uid = usualPurchase.getUser_id().getId();
        String pid = usualPurchase.getProduct_id().getId();
        Optional<UsualPurchase> existing = repository.findByUserIdAndProductId(uid, pid);
        if (existing.isPresent()) {
            UsualPurchase up = existing.get();
            up.setTarget_quantity(usualPurchase.getTarget_quantity());
            return repository.save(up);
        }
        return repository.save(usualPurchase);
    }

    private static void requireUserAndProductIds(UsualPurchase usualPurchase) {
        if (usualPurchase.getUser_id() == null || usualPurchase.getUser_id().getId() == null
                || usualPurchase.getUser_id().getId().isBlank()) {
            throw new IllegalArgumentException("user_id es obligatorio");
        }
        if (usualPurchase.getProduct_id() == null || usualPurchase.getProduct_id().getId() == null
                || usualPurchase.getProduct_id().getId().isBlank()) {
            throw new IllegalArgumentException("product_id es obligatorio");
        }
    }

    @Override
    public UsualPurchase update(String id, UsualPurchase usualPurchase) {
        findById(id);
        usualPurchase.setId(id);
        return repository.save(usualPurchase);
    }

    @Override
    public UsualPurchase updateForUser(String id, UsualPurchase usualPurchase, String userId) {
        UsualPurchase existing = findByIdForUser(id, userId);
        usualPurchase.setId(id);
        usualPurchase.setUser_id(existing.getUser_id());
        return repository.save(usualPurchase);
    }

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }

    @Override
    public void deleteForUser(String id, String userId) {
        findByIdForUser(id, userId);
        repository.deleteById(id);
    }
}