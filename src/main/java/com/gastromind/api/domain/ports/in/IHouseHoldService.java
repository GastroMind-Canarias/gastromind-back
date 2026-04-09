package com.gastromind.api.domain.ports.in;

import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.HouseholdAppliance;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Appliance;

import java.util.List;

public interface IHouseHoldService {
    List<HouseHold> findAll();

    HouseHold findById(String id);

    HouseHold create(HouseHold houseHold);

    void delete(String id);

    void removeMember(String ownerId, String memberUserId);

    User promoteToOwner(String householdId, String userId);

    User addMemberByToken(String token, String userId);

    HouseholdAppliance addAppliance(String householdId, Appliance appliance);

    /** Añade varios tipos; ignora los que ya existen en el hogar. */
    List<HouseholdAppliance> addAppliancesBulk(String householdId, List<Appliance> appliances);

    void removeApplianceFromHousehold(String householdId, String applianceRecordId);

    /** Elimina por ids de fila; cada uno debe pertenecer al hogar. */
    void removeAppliancesBulk(String householdId, List<String> applianceRecordIds);

    /** Sustituye el conjunto de tipos del hogar por el listado (puede quedar vacío). */
    List<HouseholdAppliance> replaceAppliances(String householdId, List<Appliance> appliances);

    /** Cambia el tipo de un registro concreto (sin duplicar tipo en el mismo hogar). */
    HouseholdAppliance updateAppliance(String householdId, String applianceRecordId, Appliance appliance);

    List<HouseholdAppliance> listAppliances(String householdId);

    List<User> listMembers(String householdId);

    String generateInviteToken(String householdId);

    User addMember(String householdId, String userId);

    void leaveHousehold(String userId);
}