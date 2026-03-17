package com.gastromind.api.domain.ports.in;

import java.util.List;

import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.User;

import com.gastromind.api.domain.models.HouseholdAppliance;
import com.gastromind.api.domain.models.enums.Appliance;

public interface IHouseHoldService {
    List<HouseHold> findAll();

    HouseHold findById(String id);

    HouseHold create(HouseHold houseHold);

    HouseHold update(String id, HouseHold houseHold);

    void delete(String id);

    // New methods
    HouseHold createWithCreator(HouseHold houseHold, String creatorUserId);

    HouseholdAppliance addAppliance(String householdId, Appliance appliance);

    void removeAppliance(String applianceId);

    List<HouseholdAppliance> listAppliances(String householdId);

    List<User> listMembers(String householdId);

    String generateInviteToken(String householdId);

    void removeMember(String ownerId, String householdId, String memberUserId);

    User addMember(String householdId, String userId);
}