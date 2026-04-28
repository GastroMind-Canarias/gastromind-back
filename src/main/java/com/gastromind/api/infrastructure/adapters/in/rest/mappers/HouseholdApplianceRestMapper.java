package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.HouseholdAppliance;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.household.ApplianceResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
/**
 * Define el contrato de household appliance rest.
 */
public interface HouseholdApplianceRestMapper {
    ApplianceResponse toResponse(HouseholdAppliance domain);

    List<ApplianceResponse> toResponseList(List<HouseholdAppliance> domainList);
}






