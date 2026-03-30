package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.HouseholdAppliance;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdApplianceEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = { HouseholdEntity.class })
public interface HouseholdApplianceMapper {

    @Mapping(target = "household", expression = "java(domain.getHouseholdId() != null ? new HouseholdEntity(domain.getHouseholdId()) : null)")
    HouseholdApplianceEntity toEntity(HouseholdAppliance domain);

    @Mapping(target = "householdId", source = "household.id")
    HouseholdAppliance toDomain(HouseholdApplianceEntity entity);

    List<HouseholdApplianceEntity> toEntityList(List<HouseholdAppliance> domainList);
    List<HouseholdAppliance> toDomainList(List<HouseholdApplianceEntity> entityList);
}
