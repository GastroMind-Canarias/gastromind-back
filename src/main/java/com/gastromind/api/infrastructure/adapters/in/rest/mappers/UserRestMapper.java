package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenResponse;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UserRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
/**
 * Define el contrato de user rest.
 */
public interface UserRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "houseHold_id.id", source = "houseHold_id")
    User toDomain(UserRequest request);

    Allergen toDomain(AllergenResponse response);

    @Mapping(target = "houseHold_id", source = "houseHold_id.id")
    UserResponse toResponse(User domain);

    List<UserResponse> toResponseList(List<User> users);
}






