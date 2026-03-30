package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdApplianceEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HouseholdMapper {
    @Mapping(source = "members", target = "members_count")
    @Mapping(target = "appliances", ignore = true)
    HouseholdEntity toEntity(HouseHold domain);

    @Mapping(source = "members_count", target = "members")
    @Mapping(target = "appliances", source = "appliances")
    HouseHold toDomain(HouseholdEntity entity);

    default Appliance mapApplianceEntityToEnum(HouseholdApplianceEntity entity) {
        if (entity == null || entity.getAppliance() == null) {
            return null;
        }
        return Appliance.valueOf(entity.getAppliance().name());
    }

    List<HouseholdEntity> toEntityList(List<HouseHold> domainList);
    List<HouseHold> toDomainList(List<HouseholdEntity> entityList);
}