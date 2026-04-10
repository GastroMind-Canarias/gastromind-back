package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.application.services.TicketQuantityUnitResolver;
import com.gastromind.api.domain.models.Product;
import com.gastromind.api.domain.models.Store;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.domain.models.TicketItem;
import com.gastromind.api.domain.models.Unit;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.TicketLineVerificationStatus;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketItemRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketItemResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketMeRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "total_amount", source = "total_mount")
    @Mapping(target = "user_id.id", source = "user_id")
    @Mapping(target = "store_id.id", source = "store_id")
    @Mapping(target = "items", expression = "java(mapTicketItemRequests(request.items()))")
    Ticket toDomain(TicketRequest request);

    @Mapping(target = "household_id", source = "houseHold_id.id")
    @Mapping(target = "uploaded_by_user_id", source = "user_id.id")
    @Mapping(target = "user_id", source = "user_id.id")
    @Mapping(target = "store_id", source = "store_id.id")
    @Mapping(target = "items", source = "items")
    TicketResponse toResponse(Ticket domain);

    List<TicketResponse> toResponseList(List<Ticket> tickets);

    default Ticket toDomainForMe(TicketMeRequest request, String userId) {
        Ticket t = new Ticket();
        t.setUser_id(new User(userId));
        t.setStore_id(new Store(request.store_id()));
        t.setTotal_amount(request.total_mount());
        t.setPurchaseDate(request.purchaseDate());
        t.setItems(mapTicketItemRequests(request.items()));
        return t;
    }

    List<TicketItemResponse> toItemResponseList(List<TicketItem> items);

    default TicketItemResponse toItemResponse(TicketItem item) {
        if (item == null) {
            return null;
        }
        Unit u = item.getUnit();
        String unitId = u != null ? u.getId() : null;
        String unitName = u != null ? u.getName() : null;
        String unitCode = TicketQuantityUnitResolver.canonicalCode(u);
        String verificationStatus = item.getVerificationStatus() != null
                ? item.getVerificationStatus().name()
                : TicketLineVerificationStatus.OK.name();
        boolean productNeedsReview = item.getProduct() != null && item.getProduct().isNeedsReview();
        String productReviewNote = item.getProduct() != null ? item.getProduct().getReviewNote() : null;
        return new TicketItemResponse(
                item.getId(),
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getProduct() != null ? item.getProduct().getName() : null,
                productNeedsReview,
                productReviewNote,
                item.getQuantity(),
                unitId,
                unitName,
                unitCode,
                item.getPriceUnit(),
                verificationStatus,
                item.getLineNote());
    }

    @AfterMapping
    default void ensureItemsNotNull(@MappingTarget Ticket ticket) {
        if (ticket.getItems() == null) {
            ticket.setItems(new ArrayList<>());
        }
    }

    default List<TicketItem> mapTicketItemRequests(List<TicketItemRequest> lines) {
        if (lines == null || lines.isEmpty()) {
            return new ArrayList<>();
        }
        List<TicketItem> out = new ArrayList<>();
        for (TicketItemRequest line : lines) {
            TicketItem ti = new TicketItem();
            Product p = new Product(line.product_id());
            ti.setProduct(p);
            ti.setQuantity(line.quantity());
            ti.setPriceUnit(line.price_unit());
            if (line.unit_id() != null && !line.unit_id().isBlank()) {
                ti.setUnit(new Unit(line.unit_id()));
            }
            if (line.verification_status() != null && !line.verification_status().isBlank()) {
                try {
                    ti.setVerificationStatus(TicketLineVerificationStatus.valueOf(line.verification_status().trim()));
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (line.line_note() != null && !line.line_note().isBlank()) {
                ti.setLineNote(line.line_note().trim());
            }
            out.add(ti);
        }
        return out;
    }
}
