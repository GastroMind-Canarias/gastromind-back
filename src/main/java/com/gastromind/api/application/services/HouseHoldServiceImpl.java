package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.ForbiddenException;
import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Fridge;
import com.gastromind.api.domain.models.HouseHold;
import com.gastromind.api.domain.models.HouseholdAppliance;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Appliance;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.in.IHouseHoldService;
import com.gastromind.api.domain.ports.out.FridgeRepository;
import com.gastromind.api.domain.ports.out.HouseHoldRepository;
import com.gastromind.api.domain.ports.out.HouseholdApplianceRepository;
import com.gastromind.api.domain.ports.out.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class HouseHoldServiceImpl implements IHouseHoldService {
    private static final String INVITE_TOKEN_PREFIX = "invite_";
    private static final String INVITE_TOKEN_SEPARATOR = "_";

    private final HouseHoldRepository repository;
    private final UserRepository userRepository;
    private final HouseholdApplianceRepository applianceRepository;
    private final FridgeRepository fridgeRepository;

    public HouseHoldServiceImpl(HouseHoldRepository repository, UserRepository userRepository,
            HouseholdApplianceRepository applianceRepository, FridgeRepository fridgeRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.applianceRepository = applianceRepository;
        this.fridgeRepository = fridgeRepository;
    }

    @Override
    public List<HouseHold> findAll() {
        return repository.findAll();
    }

    @Override
    public HouseHold findById(String id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Unidad Familiar no encontrada"));
    }

    @Override
    public HouseHold create(HouseHold houseHold) {
        HouseHold savedHouseHold = repository.save(houseHold);
        createFridge(savedHouseHold);
        return savedHouseHold;
    }

    private void createFridge(HouseHold houseHold) {
        Fridge fridge = new Fridge();
        fridge.setHouseHold_id(houseHold);
        fridgeRepository.save(fridge);
    }

    @Override
    public void delete(String id) {
        HouseHold houseHold = findById(id);
        List<User> members = userRepository.findByHouseholdId(id);
        members.forEach(m -> {
            m.setHouseHold_id(null);
            m.setRole(Role.ROLE_MEMBER);
            userRepository.save(m);
        });
        repository.deleteById(id);
    }

    @Override
    public void removeMember(String householdId, String memberUserId) {
        User member = userRepository.findById(memberUserId)
                .orElseThrow(() -> new NotFoundException("Miembro no encontrado"));

        if (member.getHouseHold_id() == null || !member.getHouseHold_id().getId().equals(householdId)) {
            throw new NotFoundException("El usuario no pertenece a este hogar");
        }

        member.setHouseHold_id(null);
        member.setRole(Role.ROLE_MEMBER);
        userRepository.save(member);
    }

    @Override
    public User promoteToOwner(String householdId, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (user.getHouseHold_id() == null || !user.getHouseHold_id().getId().equals(householdId)) {
            throw new ForbiddenException("El usuario debe pertenecer al hogar para ser promovido");
        }

        user.setRole(Role.ROLE_OWNER);
        return userRepository.save(user);
    }

    @Override
    public User addMemberByToken(String token, String userId) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token de invitación es obligatorio");
        }
        if (!token.startsWith(INVITE_TOKEN_PREFIX)) {
            throw new IllegalArgumentException("El token de invitación no tiene un formato válido");
        }

        int separatorIndex = token.lastIndexOf(INVITE_TOKEN_SEPARATOR);
        if (separatorIndex < 0 || separatorIndex == token.length() - 1) {
            throw new IllegalArgumentException("El token de invitación no contiene un hogar válido");
        }

        String householdId = token.substring(separatorIndex + 1);
        if (householdId.isBlank()) {
            throw new IllegalArgumentException("El token de invitación no contiene un hogar válido");
        }

        return addMember(householdId, userId);
    }

    @Override
    public HouseholdAppliance addAppliance(String householdId, Appliance appliance) {
        findById(householdId);
        HouseholdAppliance newAppliance = new HouseholdAppliance();
        newAppliance.setAppliance(appliance);
        newAppliance.setHouseholdId(householdId);
        return applianceRepository.save(newAppliance);
    }

    @Override
    public void removeAppliance(String applianceId) {
        applianceRepository.deleteById(applianceId);
    }

    @Override
    public List<HouseholdAppliance> listAppliances(String householdId) {
        return applianceRepository.findByHouseholdId(householdId);
    }

    @Override
    public List<User> listMembers(String householdId) {
        return userRepository.findByHouseholdId(householdId);
    }

    @Override
    public String generateInviteToken(String householdId) {
        findById(householdId);
        return INVITE_TOKEN_PREFIX + UUID.randomUUID() + INVITE_TOKEN_SEPARATOR + householdId;
    }

    @Override
    public User addMember(String householdId, String userId) {
        HouseHold houseHold = findById(householdId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        user.setHouseHold_id(houseHold);
        user.setRole(Role.ROLE_MEMBER);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void leaveHousehold(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (user.getHouseHold_id() == null) {
            throw new ForbiddenException("El usuario no pertenece a ningún hogar");
        }

        user.setHouseHold_id(null);
        user.setRole(Role.ROLE_MEMBER);
        userRepository.save(user);
    }
}