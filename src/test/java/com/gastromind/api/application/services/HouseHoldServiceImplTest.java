package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.HouseholdAppliance;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.domain.ports.out.HouseHoldRepository;
import com.gastromind.api.domain.ports.out.HouseholdApplianceRepository;
import com.gastromind.api.domain.ports.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.inOrder;
import org.mockito.InOrder;

@ExtendWith(MockitoExtension.class)
class HouseHoldServiceImplTest {

    @Mock
    private HouseHoldRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HouseholdApplianceRepository applianceRepository;
    @Mock
    private FridgeRepository fridgeRepository;

    @InjectMocks
    private HouseHoldServiceImpl service;

    private HouseHold houseHold;
    private User member;

    @BeforeEach
    void setUp() {
        houseHold = new HouseHold("h-1");
        houseHold.setName("Home");

        member = new User("u-1");
        member.setHouseHold_id(houseHold);
        member.setRole(Role.ROLE_MEMBER);
    }

    @Test
    void findAll_and_findById() {
        when(repository.findAll()).thenReturn(List.of(houseHold));
        assertEquals(List.of(houseHold), service.findAll());

        when(repository.findById("h-1")).thenReturn(Optional.of(houseHold));
        assertEquals(houseHold, service.findById("h-1"));
    }

    @Test
    void findById_throwsWhenMissing() {
        when(repository.findById("x")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById("x"));
    }

    @Test
    void create_savesHouseholdAndCreatesFridge() {
        HouseHold in = new HouseHold();
        when(repository.save(in)).thenReturn(houseHold);
        when(fridgeRepository.save(any(Fridge.class))).thenAnswer(inv -> inv.getArgument(0));

        HouseHold out = service.create(in);

        assertEquals(houseHold, out);
        verify(fridgeRepository).save(any(Fridge.class));
    }

    @Test
    void delete_clearsMembersAndDeletesRelated() {
        when(repository.existsById("h-1")).thenReturn(true);
        when(userRepository.findByHouseholdId("h-1")).thenReturn(List.of(member));
        when(fridgeRepository.findByHouseholdId("h-1")).thenReturn(List.of(new Fridge("f-1")));

        service.delete("h-1");

        verify(userRepository).save(member);
        verify(applianceRepository).deleteAllByHouseholdId("h-1");
        verify(fridgeRepository).deleteById("f-1");
        verify(repository).deleteById("h-1");
    }

    @Test
    void removeMember_throwsWhenUserNotInHousehold() {
        User u = new User("u-2");
        u.setHouseHold_id(new HouseHold("other"));
        when(userRepository.findById("u-2")).thenReturn(Optional.of(u));

        assertThrows(NotFoundException.class, () -> service.removeMember("h-1", "u-2"));
    }

    @Test
    void promoteToOwner_throwsWhenUserNotInHousehold() {
        User u = new User("u-2");
        u.setHouseHold_id(new HouseHold("other"));
        when(userRepository.findById("u-2")).thenReturn(Optional.of(u));

        assertThrows(ForbiddenException.class, () -> service.promoteToOwner("h-1", "u-2"));
    }

    @Test
    void promoteToOwner_setsOwner() {
        when(userRepository.findById("u-1")).thenReturn(Optional.of(member));
        when(userRepository.save(member)).thenReturn(member);

        User out = service.promoteToOwner("h-1", "u-1");
        assertEquals(Role.ROLE_OWNER, out.getRole());
    }

    @Test
    void addMemberByToken_invalidCases() {
        assertThrows(IllegalArgumentException.class, () -> service.addMemberByToken(null, "u-1"));
        assertThrows(IllegalArgumentException.class, () -> service.addMemberByToken("   ", "u-1"));
        assertThrows(IllegalArgumentException.class, () -> service.addMemberByToken("bad", "u-1"));
        assertThrows(IllegalArgumentException.class, () -> service.addMemberByToken("invite_", "u-1"));
    }

    @Test
    void addMemberByToken_delegatesToAddMember() {
        when(repository.findById("h-1")).thenReturn(Optional.of(houseHold));
        when(userRepository.findById("u-1")).thenReturn(Optional.of(new User("u-1")));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        String token = "invite_abc_h-1";
        User out = service.addMemberByToken(token, "u-1");
        assertEquals(houseHold, out.getHouseHold_id());
    }

    @Test
    void addAppliance_throwsWhenDuplicateType() {
        when(repository.existsById("h-1")).thenReturn(true);
        HouseholdAppliance existingRow = new HouseholdAppliance("x", Appliance.HORNO, "h-1");
        when(applianceRepository.findByHouseholdId("h-1")).thenReturn(List.of(existingRow));

        assertThrows(IllegalArgumentException.class, () -> service.addAppliance("h-1", Appliance.HORNO));
    }

    @Test
    void addAppliance_saves() {
        when(repository.existsById("h-1")).thenReturn(true);
        when(applianceRepository.findByHouseholdId("h-1")).thenReturn(List.of());
        HouseholdAppliance saved = new HouseholdAppliance("ap-1", Appliance.MICROONDAS, "h-1");
        when(applianceRepository.save(any(HouseholdAppliance.class))).thenReturn(saved);

        assertEquals(saved, service.addAppliance("h-1", Appliance.MICROONDAS));
    }

    @Test
    void addAppliancesBulk_emptyReturnsCurrentList() {
        when(repository.existsById("h-1")).thenReturn(true);
        HouseholdAppliance existing = new HouseholdAppliance("ap-existing", Appliance.HORNO, "h-1");
        when(applianceRepository.findByHouseholdId("h-1")).thenReturn(List.of(existing));

        assertEquals(List.of(existing), service.addAppliancesBulk("h-1", null));
        assertEquals(List.of(existing), service.addAppliancesBulk("h-1", List.of()));
        verify(applianceRepository, never()).deleteAllByHouseholdId("h-1");
    }

    @Test
    void addAppliancesBulk_nonEmptyReplacesAllAndDeduplicates() {
        when(repository.existsById("h-1")).thenReturn(true);

        HouseholdAppliance savedHorno = new HouseholdAppliance("ap-1", Appliance.HORNO, "h-1");
        HouseholdAppliance savedMicro = new HouseholdAppliance("ap-2", Appliance.MICROONDAS, "h-1");

        when(applianceRepository.save(any(HouseholdAppliance.class)))
                .thenReturn(savedHorno)
                .thenReturn(savedMicro);

        List<HouseholdAppliance> out = service.addAppliancesBulk(
                "h-1",
                List.of(Appliance.HORNO, Appliance.HORNO, Appliance.MICROONDAS)
        );

        InOrder orderedCalls = inOrder(applianceRepository);
        orderedCalls.verify(applianceRepository).deleteAllByHouseholdId("h-1");
        orderedCalls.verify(applianceRepository, times(2)).save(any(HouseholdAppliance.class));
        verify(applianceRepository).deleteAllByHouseholdId("h-1");
        ArgumentCaptor<HouseholdAppliance> captor = ArgumentCaptor.forClass(HouseholdAppliance.class);
        verify(applianceRepository, times(2)).save(captor.capture());
        List<HouseholdAppliance> savedRows = captor.getAllValues();
        assertEquals(Appliance.HORNO, savedRows.get(0).getAppliance());
        assertEquals(Appliance.MICROONDAS, savedRows.get(1).getAppliance());
        assertEquals("h-1", savedRows.get(0).getHouseholdId());
        assertEquals("h-1", savedRows.get(1).getHouseholdId());
        assertEquals(List.of(savedHorno, savedMicro), out);
    }

    @Test
    void addAppliancesBulk_replaceFromAirfryerAndHornoToOnlyHorno() {
        when(repository.existsById("h-1")).thenReturn(true);
        HouseholdAppliance savedHorno = new HouseholdAppliance("ap-new", Appliance.HORNO, "h-1");
        when(applianceRepository.save(any(HouseholdAppliance.class))).thenReturn(savedHorno);

        List<HouseholdAppliance> out = service.addAppliancesBulk("h-1", List.of(Appliance.HORNO));

        InOrder orderedCalls = inOrder(applianceRepository);
        orderedCalls.verify(applianceRepository).deleteAllByHouseholdId("h-1");
        orderedCalls.verify(applianceRepository).save(any(HouseholdAppliance.class));

        ArgumentCaptor<HouseholdAppliance> captor = ArgumentCaptor.forClass(HouseholdAppliance.class);
        verify(applianceRepository).save(captor.capture());
        assertEquals(Appliance.HORNO, captor.getValue().getAppliance());
        assertEquals("h-1", captor.getValue().getHouseholdId());
        assertEquals(List.of(savedHorno), out);
    }

    @Test
    void replaceAppliances_deletesAndRecreates() {
        when(repository.existsById("h-1")).thenReturn(true);
        HouseholdAppliance row = new HouseholdAppliance();
        when(applianceRepository.save(any(HouseholdAppliance.class))).thenReturn(row);

        List<HouseholdAppliance> out = service.replaceAppliances("h-1", List.of(Appliance.HORNO, Appliance.HORNO));
        verify(applianceRepository).deleteAllByHouseholdId("h-1");
        assertEquals(1, out.size());
    }

    @Test
    void removeAppliancesBulk_skipsNullAndBlank() {
        when(repository.existsById("h-1")).thenReturn(true);
        service.removeAppliancesBulk("h-1", null);

        HouseholdAppliance ha = new HouseholdAppliance("rid", Appliance.HORNO, "h-1");
        when(applianceRepository.findById("rid")).thenReturn(Optional.of(ha));
        service.removeAppliancesBulk("h-1", Arrays.asList("rid", "", null));

        verify(applianceRepository).deleteById("rid");
    }

    @Test
    void removeAppliancesBulk_forbiddenWhenWrongHousehold() {
        when(repository.existsById("h-1")).thenReturn(true);
        HouseholdAppliance ha = new HouseholdAppliance("rid", Appliance.HORNO, "other");
        when(applianceRepository.findById("rid")).thenReturn(Optional.of(ha));

        assertThrows(ForbiddenException.class, () -> service.removeAppliancesBulk("h-1", List.of("rid")));
    }

    @Test
    void updateAppliance_throwsWhenDuplicateTypeExists() {
        when(repository.existsById("h-1")).thenReturn(true);
        HouseholdAppliance ha = new HouseholdAppliance("rid", Appliance.HORNO, "h-1");
        HouseholdAppliance other = new HouseholdAppliance("r2", Appliance.MICROONDAS, "h-1");
        when(applianceRepository.findById("rid")).thenReturn(Optional.of(ha));
        when(applianceRepository.findByHouseholdId("h-1")).thenReturn(List.of(ha, other));

        assertThrows(IllegalArgumentException.class, () -> service.updateAppliance("h-1", "rid", Appliance.MICROONDAS));
    }

    @Test
    void generateInviteToken_returnsPrefixedToken() {
        when(repository.existsById("h-1")).thenReturn(true);

        String token = service.generateInviteToken("h-1");
        assertTrue(token.startsWith("invite_"));
        assertTrue(token.endsWith("_h-1"));
    }

    @Test
    void listMembers_and_listAppliances() {
        when(userRepository.findByHouseholdId("h-1")).thenReturn(List.of(member));
        assertEquals(List.of(member), service.listMembers("h-1"));

        HouseholdAppliance ha = new HouseholdAppliance("x", Appliance.HORNO, "h-1");
        when(applianceRepository.findByHouseholdId("h-1")).thenReturn(List.of(ha));
        assertEquals(List.of(ha), service.listAppliances("h-1"));
    }

    @Test
    void leaveHousehold_throwsWhenNoHousehold() {
        User u = new User("u-1");
        when(userRepository.findById("u-1")).thenReturn(Optional.of(u));

        assertThrows(ForbiddenException.class, () -> service.leaveHousehold("u-1"));
    }

    @Test
    void leaveHousehold_deletesHouseholdWhenLastMember() {
        member.setRole(Role.ROLE_MEMBER);
        when(userRepository.findById("u-1")).thenReturn(Optional.of(member));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findByHouseholdId("h-1")).thenReturn(List.of());

        service.leaveHousehold("u-1");

        verify(applianceRepository).deleteAllByHouseholdId("h-1");
        verify(repository).deleteById("h-1");
    }

    @Test
    void leaveHousehold_promotesRandomOwnerWhenOwnerLeaves() {
        User owner = new User("u-owner");
        owner.setHouseHold_id(houseHold);
        owner.setRole(Role.ROLE_OWNER);

        User other = new User("u-other");
        other.setHouseHold_id(houseHold);
        other.setRole(Role.ROLE_MEMBER);

        when(userRepository.findById("u-owner")).thenReturn(Optional.of(owner));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findByHouseholdId("h-1")).thenReturn(List.of(other));

        service.leaveHousehold("u-owner");

        assertEquals(Role.ROLE_OWNER, other.getRole());
    }

    @Test
    void removeApplianceFromHousehold_deletesWhenOwned() {
        when(repository.existsById("h-1")).thenReturn(true);
        HouseholdAppliance ha = new HouseholdAppliance("rid", Appliance.HORNO, "h-1");
        when(applianceRepository.findById("rid")).thenReturn(Optional.of(ha));

        service.removeApplianceFromHousehold("h-1", "rid");
        verify(applianceRepository).deleteById("rid");
    }
}
