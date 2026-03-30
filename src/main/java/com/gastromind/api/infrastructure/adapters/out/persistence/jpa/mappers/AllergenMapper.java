package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.AllergenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AllergenMapper {

    AllergenEntity toEntity(Allergen domain);
    Allergen toDomain(AllergenEntity entity);

    List<AllergenEntity> toEntityList(List<Allergen> domainList);
    List<Allergen> toDomainList(List<AllergenEntity> entityList);
}