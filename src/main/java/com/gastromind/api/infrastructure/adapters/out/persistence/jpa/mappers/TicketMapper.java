package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketEntity;

// Usamos los mappers de User y Store para resolver las relaciones internas
@Mapper(componentModel = "spring", uses = {UserMapper.class, StoreMapper.class})
public interface TicketMapper {

    @Mapping(source = "user_id", target = "user")
    @Mapping(source = "store_id", target = "store")
    @Mapping(source = "total_amount", target = "totalAmount")
    @Mapping(source = "purchaseDate", target = "purchaseDate")
    TicketEntity toEntity(Ticket domain);

    @Mapping(source = "user", target = "user_id")
    @Mapping(source = "store", target = "store_id")
    @Mapping(source = "totalAmount", target = "total_amount")
    @Mapping(source = "purchaseDate", target = "purchaseDate")
    Ticket toDomain(TicketEntity entity);

    List<TicketEntity> toEntityList(List<Ticket> domainList);
    List<Ticket> toDomainList(List<TicketEntity> entityList);
}