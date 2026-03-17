package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Ticket;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.ticket.TicketResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketRestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "total_amount", source = "total_mount")
    @Mapping(target = "user_id.id", source = "user_id")
    @Mapping(target = "store_id.id", source = "store_id")
    Ticket toDomain(TicketRequest request);

    @Mapping(target = "user_id", source = "user_id.id")
    @Mapping(target = "store_id", source = "store_id.id")
    TicketResponse toResponse(Ticket domain);

    List<TicketResponse> toResponseList(List<Ticket> tickets);
}
