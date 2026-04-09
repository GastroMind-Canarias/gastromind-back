package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;

// Usamos los mappers de User y Store para resolver las relaciones internas
@Mapper(componentModel = "spring", uses = { UserMapper.class, StoreMapper.class, TicketItemMapper.class })
public interface TicketMapper {

    @Mapping(source = "user_id", target = "user")
    @Mapping(source = "store_id", target = "store")
    @Mapping(source = "total_amount", target = "totalAmount")
    @Mapping(target = "purchaseDate", expression = "java(domain.getPurchaseDate() != null ? domain.getPurchaseDate().atStartOfDay() : null)")
    @Mapping(source = "items", target = "items")
    TicketEntity toEntity(Ticket domain);

    @Mapping(source = "user", target = "user_id")
    @Mapping(source = "store", target = "store_id")
    @Mapping(source = "totalAmount", target = "total_amount")
    @Mapping(target = "purchaseDate", expression = "java(entity.getPurchaseDate() != null ? entity.getPurchaseDate().toLocalDate() : null)")
    @Mapping(source = "items", target = "items")
    Ticket toDomain(TicketEntity entity);

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
