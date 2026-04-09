package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.models.TicketItem;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.models.enums.TicketLineVerificationStatus;
import com.gastromind.api.domain.ports.in.ITicketService;
import com.gastromind.api.domain.ports.out.ProductRepository;
import com.gastromind.api.domain.ports.out.TicketRepository;
import com.gastromind.api.domain.ports.out.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketServiceImpl implements ITicketService {

    private final TicketRepository repository;
    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;

    public TicketServiceImpl(
            TicketRepository repository,
            ProductRepository productRepository,
            UnitRepository unitRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.unitRepository = unitRepository;
    }

    @Override
    public List<Ticket> findAll() {
        return repository.findAll();
    }

    @Override
    public Ticket findById(String id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Ticket no encontrado"));
    }

    @Override
    @Transactional
    public Ticket create(Ticket ticket) {
        resolveItemReferences(ticket);
        return repository.save(ticket);
    }

    private void resolveItemReferences(Ticket ticket) {
        if (ticket.getItems() == null || ticket.getItems().isEmpty()) {
            return;
        }
        Unit defaultUd = unitRepository.findFirstByNameIgnoreCase("Unidades")
                .orElseThrow(() -> new NotFoundException(
                        "Unidad 'Unidades' no encontrada en catálogo. Revise la tabla unit / data.sql."));
        for (TicketItem item : ticket.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                throw new IllegalArgumentException("Cada línea del ticket debe tener un producto con id");
            }
            Product fullProduct = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + item.getProduct().getId()));
            item.setProduct(fullProduct);

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
        return repository.save(ticket);
    }

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}
