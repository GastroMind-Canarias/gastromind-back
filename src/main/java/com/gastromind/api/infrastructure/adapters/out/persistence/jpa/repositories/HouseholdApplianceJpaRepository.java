package com.gastromind.api.infrastructure.adapters.out.persistence.jpa.repositories;

import com.gastromind.api.infrastructure.adapters.out.persistence.jpa.entities.HouseholdApplianceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/**
 * Define el contrato de household appliance jpa.
 */
public interface HouseholdApplianceJpaRepository extends JpaRepository<HouseholdApplianceEntity, String> {
    List<HouseholdApplianceEntity> findByHousehold_Id(String householdId);

    void deleteAllByHousehold_Id(String householdId);

    @Modifying
    @Query("DELETE FROM HouseholdApplianceEntity ha WHERE ha.household.id = :householdId")
    void deleteAllForHousehold(@Param("householdId") String householdId);
}






