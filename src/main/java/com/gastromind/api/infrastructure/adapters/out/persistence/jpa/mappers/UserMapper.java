package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdEntity;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "houseHold_id", target = "household", qualifiedByName = "toHouseholdEntityShallow")
    UserEntity toEntity(User user);

    @Mapping(source = "household", target = "houseHold_id", qualifiedByName = "toHouseholdDomainShallow")
    User toDomain(UserEntity entity);

    List<UserEntity> toEntityList(List<User> domainList);
    List<User> toDomainList(List<UserEntity> entityList);

    @org.mapstruct.Named("toHouseholdDomainShallow")
    default HouseHold toHouseholdDomainShallow(HouseholdEntity entity) {
        if (entity == null) {
            return null;
        }
        HouseHold household = new HouseHold();
        household.setId(entity.getId());
        household.setName(entity.getName());
        return household;
    }

    @org.mapstruct.Named("toHouseholdEntityShallow")
    default HouseholdEntity toHouseholdEntityShallow(HouseHold household) {
        if (household == null) {
            return null;
        }
        HouseholdEntity entity = new HouseholdEntity();
        entity.setId(household.getId());
        entity.setName(household.getName());
        return entity;
    }
}