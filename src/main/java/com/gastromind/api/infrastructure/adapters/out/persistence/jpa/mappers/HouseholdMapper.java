package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdApplianceEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.enums.ApplianceType;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
/**
 * Define el contrato de household.
 */
public interface HouseholdMapper {

    @Mapping(target = "members", source = "members")
    @Mapping(target = "appliances", source = "appliances")
    HouseholdEntity toEntity(HouseHold domain);

    @Mapping(target = "members", source = "members")
    @Mapping(target = "appliances", source = "appliances")
    HouseHold toDomain(HouseholdEntity entity);

    default Appliance mapEntityToDomainEnum(HouseholdApplianceEntity entity) {
        if (entity == null || entity.getAppliance() == null) {
            return null;
        }
        return Appliance.valueOf(entity.getAppliance().name());
    }

    default List<Appliance> mapEntityListToDomainList(List<HouseholdApplianceEntity> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::mapEntityToDomainEnum)
                .collect(Collectors.toList());
    }

    default HouseholdApplianceEntity mapDomainEnumToEntity(Appliance appliance) {
        if (appliance == null) return null;
        HouseholdApplianceEntity entity = new HouseholdApplianceEntity();
        entity.setAppliance(ApplianceType.valueOf(appliance.name()));
        return entity;
    }

    default List<HouseholdApplianceEntity> mapDomainListToEntityList(List<Appliance> appliances, @Context HouseholdEntity parent) {
        if (appliances == null) return null;
        return appliances.stream()
                .map(a -> {
                    HouseholdApplianceEntity entity = new HouseholdApplianceEntity();
                    entity.setAppliance(ApplianceType.valueOf(a.name()));
                    entity.setHousehold(parent);
                    return entity;
                })
                .collect(Collectors.toList());
    }

    List<HouseholdEntity> toEntityList(List<HouseHold> domainList);
    List<HouseHold> toDomainList(List<HouseholdEntity> entityList);
}






