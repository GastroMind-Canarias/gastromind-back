package com.gastromind.api.infrastructure.adapters.in.rest.mappers;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenRequest;
import com.gastromind.api.infrastructure.adapters.in.rest.dtos.allergen.AllergenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AllergenRestMapper {

    @Mapping(target = "id", ignore = true)
    Allergen toDomain(AllergenRequest request);

    AllergenResponse toResponse(Allergen domain);

    List<AllergenResponse> toResponseList(List<Allergen> allergens);
}
