package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.models.TicketItem;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.models.enums.TicketLineVerificationStatus;
import com.gastromind.api.domain.ports.in.ITicketService;
import com.gastromind.api.domain.ports.out.ProductRepository;
import com.gastromind.api.domain.ports.out.TicketRepository;
import com.gastromind.api.domain.ports.out.UnitRepository;
import com.gastromind.api.domain.ports.out.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketServiceImpl implements ITicketService {

    private final TicketRepository repository;
    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final UsualPurchaseTicketSyncService usualPurchaseTicketSyncService;

    public TicketServiceImpl(
            TicketRepository repository,
            ProductRepository productRepository,
            UnitRepository unitRepository,
            UserRepository userRepository,
            UsualPurchaseTicketSyncService usualPurchaseTicketSyncService) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
        this.usualPurchaseTicketSyncService = usualPurchaseTicketSyncService;
    }

    @Override
    public List<Ticket> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Ticket> findAllByUserId(String userId) {
        return repository.findAllByUserId(userId);
    }

    @Override
    public List<Ticket> findAllVisibleForUserHousehold(String userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        if (u.getHouseHold_id() == null || u.getHouseHold_id().getId() == null) {
            throw new ForbiddenException("El usuario no pertenece a ningún hogar");
        }
        return repository.findVisibleForHousehold(u.getHouseHold_id().getId());
    }

    @Override
    public Ticket findById(String id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Ticket no encontrado"));
    }

    @Override
    public Ticket findByIdForUser(String ticketId, String userId) {
        Ticket ticket = findById(ticketId);
        requireTicketOwner(ticket, userId);
        return ticket;
    }

    @Override
    public Ticket findByIdForHouseholdMember(String ticketId, String userId) {
        Ticket ticket = findById(ticketId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        if (user.getHouseHold_id() == null || user.getHouseHold_id().getId() == null) {
            throw new ForbiddenException("El usuario no pertenece a ningún hogar");
        }
        String householdId = user.getHouseHold_id().getId();
        if (ticket.getHouseHold_id() != null && ticket.getHouseHold_id().getId() != null) {
            if (!ticket.getHouseHold_id().getId().equals(householdId)) {
                throw new ForbiddenException("No tiene acceso a este ticket");
            }
            return ticket;
        }
        if (ticket.getUser_id() == null || ticket.getUser_id().getId() == null) {
            throw new ForbiddenException("No tiene acceso a este ticket");
        }
        User uploader = userRepository.findById(ticket.getUser_id().getId())
                .orElseThrow(() -> new ForbiddenException("No tiene acceso a este ticket"));
        if (uploader.getHouseHold_id() == null || uploader.getHouseHold_id().getId() == null
                || !uploader.getHouseHold_id().getId().equals(householdId)) {
            throw new ForbiddenException("No tiene acceso a este ticket");
        }
        return ticket;
    }

    private static void requireTicketOwner(Ticket ticket, String userId) {
        if (ticket.getUser_id() == null || ticket.getUser_id().getId() == null
                || !ticket.getUser_id().getId().equals(userId)) {
            throw new ForbiddenException("No tiene acceso a este ticket");
        }
    }

    @Override
    @Transactional
    public Ticket create(Ticket ticket) {
        resolveItemReferences(ticket);
        attachHouseholdFromUploader(ticket);
        Ticket saved = repository.save(ticket);
        usualPurchaseTicketSyncService.syncAfterTicketCreated(saved);
        return saved;
    }

    private void attachHouseholdFromUploader(Ticket ticket) {
        if (ticket.getUser_id() == null || ticket.getUser_id().getId() == null) {
            return;
        }
        userRepository.findById(ticket.getUser_id().getId()).ifPresent(u -> {
            if (u.getHouseHold_id() != null && u.getHouseHold_id().getId() != null) {
                ticket.setHouseHold_id(u.getHouseHold_id());
            }
        });
    }

    private void resolveItemReferences(Ticket ticket) {
        if (ticket.getItems() == null || ticket.getItems().isEmpty()) {
            return;
        }
        Unit defaultUd = unitRepository.findFirstByNameIgnoreCase("Unidades")
                .orElseThrow(() -> new NotFoundException(
                        "Unidad 'Unidades' no encontrada en catálogo. Revise la tabla unit / data.sql."));
        for (TicketItem item : ticket.getItems()) {
            boolean hasProductId = item.getProduct() != null && item.getProduct().getId() != null;
            boolean hasLineName = item.getLineProductName() != null && !item.getLineProductName().isBlank();
            if (!hasProductId && !hasLineName) {
                throw new IllegalArgumentException(
                        "Cada línea del ticket debe tener un producto con id o un nombre de línea (sin catálogo)");
            }
            if (hasProductId) {
                Product fullProduct = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + item.getProduct().getId()));
                item.setProduct(fullProduct);
            } else {
                item.setLineProductName(item.getLineProductName().trim());
                item.setProduct(null);
            }

            if (item.getUnit() == null || item.getUnit().getId() == null) {
                item.setUnit(defaultUd);
            } else {
                Unit fullUnit = unitRepository.findById(item.getUnit().getId())
                        .orElseThrow(() -> new NotFoundException("Unidad no encontrada: " + item.getUnit().getId()));
                item.setUnit(fullUnit);
            }
            if (item.getVerificationStatus() == null) {
                item.setVerificationStatus(TicketLineVerificationStatus.OK);
            }
        }
    }

    @Override
    @Transactional
    public Ticket update(String id, Ticket ticket) {
        findById(id);
        ticket.setId(id);
        resolveItemReferences(ticket);
        attachHouseholdFromUploader(ticket);
        return repository.save(ticket);
    }

    @Override
    @Transactional
    public Ticket updateForUser(String id, Ticket ticket, String userId) {
        Ticket existing = findByIdForUser(id, userId);
        ticket.setId(id);
        ticket.setUser_id(existing.getUser_id());
        resolveItemReferences(ticket);
        attachHouseholdFromUploader(ticket);
        return repository.save(ticket);
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
