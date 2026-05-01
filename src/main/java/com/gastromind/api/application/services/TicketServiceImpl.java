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
/**
 * Servicio de aplicación para gestionar tickets de compra.
 */
public class TicketServiceImpl implements ITicketService {

    private final TicketRepository repository;
    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final UsualPurchaseTicketSyncService usualPurchaseTicketSyncService;
    /**
     * Crea el servicio con sus dependencias de persistencia y sincronización.
     * @param repository repositorio de tickets
     * @param productRepository repositorio de productos
     * @param unitRepository repositorio de unidades
     * @param userRepository repositorio de usuarios
     * @param usualPurchaseTicketSyncService servicio de sincronización de compras habituales
     */

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
    /**
     * Devuelve todos los tickets registrados.
     * @return listado completo de tickets
     */

    @Override
    public List<Ticket> findAll() {
        return repository.findAll();
    }
    /**
     * Devuelve los tickets creados por un usuario.
     * @param userId identificador del usuario
     * @return tickets del usuario
     */

    @Override
    public List<Ticket> findAllByUserId(String userId) {
        return repository.findAllByUserId(userId);
    }
    /**
     * Devuelve los tickets visibles para el hogar del usuario.
     * @param userId identificador del usuario
     * @return tickets visibles en el contexto de su hogar
     */

    @Override
    public List<Ticket> findAllVisibleForUserHousehold(String userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        if (u.getHouseHold_id() == null || u.getHouseHold_id().getId() == null) {
            throw new ForbiddenException("El usuario no pertenece a ningun hogar");
        }
        return repository.findVisibleForHousehold(u.getHouseHold_id().getId());
    }
    /**
     * Busca un ticket por su identificador.
     * @param id identificador del ticket
     * @return ticket encontrado
     * @throws NotFoundException si el ticket no existe
     */

    @Override
    public Ticket findById(String id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Ticket no encontrado"));
    }
    /**
     * Busca un ticket y valida que pertenezca al usuario indicado.
     * @param ticketId identificador del ticket
     * @param userId identificador del usuario
     * @return ticket autorizado para el usuario
     */

    @Override
    public Ticket findByIdForUser(String ticketId, String userId) {
        Ticket ticket = findById(ticketId);
        requireTicketOwner(ticket, userId);
        return ticket;
    }
    /**
     * Busca un ticket y valida acceso para un miembro del mismo hogar.
     * @param ticketId identificador del ticket
     * @param userId identificador del usuario solicitante
     * @return ticket autorizado para el hogar
     */

    @Override
    public Ticket findByIdForHouseholdMember(String ticketId, String userId) {
        Ticket ticket = findById(ticketId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        if (user.getHouseHold_id() == null || user.getHouseHold_id().getId() == null) {
            throw new ForbiddenException("El usuario no pertenece a ningun hogar");
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
    /**
     * Crea un ticket nuevo resolviendo referencias de líneas y hogar.
     * @param ticket ticket a crear
     * @return ticket persistido
     */

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
                        "Unidad 'Unidades' no encontrada en catAAaAaAaaAAaAAasAAlogo. Revise la tabla unit / data.sql."));
        for (TicketItem item : ticket.getItems()) {
            boolean hasProductId = item.getProduct() != null && item.getProduct().getId() != null;
            boolean hasLineName = item.getLineProductName() != null && !item.getLineProductName().isBlank();
            if (!hasProductId && !hasLineName) {
                throw new IllegalArgumentException(
                        "Cada lAAaAaAaaAAaAAasAAnea del ticket debe tener un producto con id o un nombre de lAAaAaAaaAAaAAasAAnea (sin catAAaAaAaaAAaAAasAAlogo)");
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
    /**
     * Define un ticket existente.
     * @param id identificador del ticket
     * @param ticket nuevos datos del ticket
     * @return ticket actualizado
     */

    @Override
    @Transactional
    public Ticket update(String id, Ticket ticket) {
        findById(id);
        ticket.setId(id);
        resolveItemReferences(ticket);
        attachHouseholdFromUploader(ticket);
        return repository.save(ticket);
    }
    /**
     * Define un ticket validando que pertenezca al usuario.
     * @param id identificador del ticket
     * @param ticket nuevos datos del ticket
     * @param userId identificador del usuario
     * @return ticket actualizado
     */

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
    /**
     * Elimina un ticket por su identificador.
     * @param id identificador del ticket
     */

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
    /**
     * Elimina un ticket validando que pertenezca al usuario.
     * @param id identificador del ticket
     * @param userId identificador del usuario
     */

    @Override
    public void deleteForUser(String id, String userId) {
        findByIdForUser(id, userId);
        repository.deleteById(id);
    }
}




