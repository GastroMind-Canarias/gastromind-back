package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.TicketItem;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.TicketItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ProductMapper.class, UnitMapper.class })
public interface TicketItemMapper {

    @Mapping(target = "ticket", ignore = true)
    TicketItemEntity toEntity(TicketItem domain);

    List<TicketItemEntity> toEntityList(List<TicketItem> items);

    TicketItem toDomain(TicketItemEntity entity);

    List<TicketItem> toDomainList(List<TicketItemEntity> entities);
}
