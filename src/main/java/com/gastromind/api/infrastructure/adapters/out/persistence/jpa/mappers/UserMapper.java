package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gastromind.api.domain.models.User;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.UserEntity;

@Mapper(componentModel = "spring", uses = {HouseholdMapper.class})
public interface UserMapper {

    @Mapping(source = "houseHold_id", target = "household")
    UserEntity toEntity(User user);

    @Mapping(source = "household", target = "houseHold_id")
    User toDomain(UserEntity entity);

    List<UserEntity> toEntityList(List<User> domainList);
    List<User> toDomainList(List<UserEntity> entityList);
}