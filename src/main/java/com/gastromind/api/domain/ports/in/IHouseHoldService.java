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

    void removeAppliance(String applianceId);

    List<HouseholdAppliance> listAppliances(String householdId);

    List<User> listMembers(String householdId);

    String generateInviteToken(String householdId);

    User addMember(String householdId, String userId);

    void leaveHousehold(String userId);
}