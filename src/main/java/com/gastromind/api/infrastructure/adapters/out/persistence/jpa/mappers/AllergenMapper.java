package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import java.util.List; // Importante añadir la importación
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.AllergenEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AllergenMapper {

    AllergenEntity toEntity(Allergen domain);
    Allergen toDomain(AllergenEntity entity);

    List<AllergenEntity> toEntityList(List<Allergen> domainList);
    List<Allergen> toDomainList(List<AllergenEntity> entityList);
}