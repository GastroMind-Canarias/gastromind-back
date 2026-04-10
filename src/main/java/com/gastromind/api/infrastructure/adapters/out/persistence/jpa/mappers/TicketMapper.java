package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

// Usamos los mappers de User y Store para resolver las relaciones internas
@Mapper(componentModel = "spring", uses = { UserMapper.class, StoreMapper.class, TicketItemMapper.class })
public interface TicketMapper {

    @Mapping(source = "user_id", target = "user")
    @Mapping(source = "houseHold_id", target = "household", qualifiedByName = "ticketHouseholdToEntity")
    @Mapping(source = "store_id", target = "store")
    @Mapping(source = "total_amount", target = "totalAmount")
    @Mapping(target = "purchaseDate", expression = "java(domain.getPurchaseDate() != null ? domain.getPurchaseDate().atStartOfDay() : null)")
    @Mapping(source = "items", target = "items")
    TicketEntity toEntity(Ticket domain);

    @Mapping(source = "user", target = "user_id")
    @Mapping(source = "household", target = "houseHold_id", qualifiedByName = "ticketHouseholdToDomain")
    @Mapping(source = "store", target = "store_id")
    @Mapping(source = "totalAmount", target = "total_amount")
    @Mapping(target = "purchaseDate", expression = "java(entity.getPurchaseDate() != null ? entity.getPurchaseDate().toLocalDate() : null)")
    @Mapping(source = "items", target = "items")
    Ticket toDomain(TicketEntity entity);

    @Named("ticketHouseholdToEntity")
    default HouseholdEntity ticketHouseholdToEntity(HouseHold h) {
        if (h == null || h.getId() == null || h.getId().isBlank()) {
            return null;
        }
        HouseholdEntity e = new HouseholdEntity();
        e.setId(h.getId());
        return e;
    }

    @Named("ticketHouseholdToDomain")
    default HouseHold ticketHouseholdToDomain(HouseholdEntity entity) {
        if (entity == null) {
            return null;
        }
        return new HouseHold(entity.getId());
    }

    List<TicketEntity> toEntityList(List<Ticket> domainList);

    List<Ticket> toDomainList(List<TicketEntity> entityList);

    @AfterMapping
    default void linkItemsToTicket(@MappingTarget TicketEntity entity) {
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> item.setTicket(entity));
        }
    }

    @AfterMapping
    default void ensureDomainItems(@MappingTarget Ticket ticket) {
        if (ticket.getItems() == null) {
            ticket.setItems(new ArrayList<>());
        }
    }
}
