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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Electrodomésticos: catálogo = enum; {@code household_appliances} solo enlaza hogar ↔ tipo.
 * Los borrados quitan esa relación, no una entidad de catálogo.
 */
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

    private void ensureHouseholdExists(String householdId) {
        if (!repository.existsById(householdId)) {
            throw new NotFoundException("Unidad Familiar no encontrada");
        }
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
        ensureHouseholdExists(id);
        List<User> members = userRepository.findByHouseholdId(id);
        members.forEach(m -> {
            m.setHouseHold_id(null);
            m.setRole(Role.ROLE_MEMBER);
            userRepository.save(m);
        });
        deleteHouseholdAndRelatedData(id);
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
        ensureHouseholdExists(householdId);
        if (applianceTypeExistsInHousehold(householdId, appliance, null)) {
            throw new IllegalArgumentException("Ese tipo de electrodoméstico ya está en el hogar");
        }
        HouseholdAppliance newAppliance = new HouseholdAppliance();
        newAppliance.setAppliance(appliance);
        newAppliance.setHouseholdId(householdId);
        return applianceRepository.save(newAppliance);
    }

    @Override
    @Transactional
    public List<HouseholdAppliance> addAppliancesBulk(String householdId, List<Appliance> appliances) {
        ensureHouseholdExists(householdId);
        if (appliances == null || appliances.isEmpty()) {
            return listAppliances(householdId);
        }
        for (Appliance a : new LinkedHashSet<>(appliances)) {
            if (!applianceTypeExistsInHousehold(householdId, a, null)) {
                HouseholdAppliance row = new HouseholdAppliance();
                row.setAppliance(a);
                row.setHouseholdId(householdId);
                applianceRepository.save(row);
            }
        }
        return listAppliances(householdId);
    }

    @Override
    @Transactional
    public void removeAppliancesBulk(String householdId, List<String> applianceRecordIds) {
        ensureHouseholdExists(householdId);
        if (applianceRecordIds == null) {
            return;
        }
        for (String rid : applianceRecordIds) {
            if (rid == null || rid.isBlank()) {
                continue;
            }
            HouseholdAppliance ha = applianceRepository.findById(rid)
                    .orElseThrow(() -> new NotFoundException("Electrodoméstico no encontrado"));
            if (!ha.getHouseholdId().equals(householdId)) {
                throw new ForbiddenException("El electrodoméstico no pertenece a este hogar");
            }
            applianceRepository.deleteById(rid);
        }
    }

    @Override
    @Transactional
    public List<HouseholdAppliance> replaceAppliances(String householdId, List<Appliance> appliances) {
        ensureHouseholdExists(householdId);
        applianceRepository.deleteAllByHouseholdId(householdId);
        if (appliances == null || appliances.isEmpty()) {
            return List.of();
        }
        List<HouseholdAppliance> saved = new ArrayList<>();
        for (Appliance ap : new LinkedHashSet<>(appliances)) {
            HouseholdAppliance row = new HouseholdAppliance();
            row.setAppliance(ap);
            row.setHouseholdId(householdId);
            saved.add(applianceRepository.save(row));
        }
        return saved;
    }

    @Override
    @Transactional
    public HouseholdAppliance updateAppliance(String householdId, String applianceRecordId, Appliance appliance) {
        ensureHouseholdExists(householdId);
        HouseholdAppliance ha = applianceRepository.findById(applianceRecordId)
                .orElseThrow(() -> new NotFoundException("Electrodoméstico no encontrado"));
        if (!ha.getHouseholdId().equals(householdId)) {
            throw new ForbiddenException("El electrodoméstico no pertenece a este hogar");
        }
        if (applianceTypeExistsInHousehold(householdId, appliance, applianceRecordId)) {
            throw new IllegalArgumentException("Ese tipo de electrodoméstico ya está en el hogar");
        }
        ha.setAppliance(appliance);
        return applianceRepository.save(ha);
    }

    private boolean applianceTypeExistsInHousehold(String householdId, Appliance type, String excludeRecordId) {
        return listAppliances(householdId).stream()
                .anyMatch(a -> a.getAppliance() == type
                        && (excludeRecordId == null || !excludeRecordId.equals(a.getId())));
    }

    @Override
    @Transactional
    public void removeApplianceFromHousehold(String householdId, String applianceRecordId) {
        ensureHouseholdExists(householdId);
        HouseholdAppliance ha = applianceRepository.findById(applianceRecordId)
                .orElseThrow(() -> new NotFoundException("Electrodoméstico no encontrado"));
        if (!ha.getHouseholdId().equals(householdId)) {
            throw new ForbiddenException("El electrodoméstico no pertenece a este hogar");
        }
        applianceRepository.deleteById(applianceRecordId);
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
        ensureHouseholdExists(householdId);
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

        if (user.getHouseHold_id() == null || user.getHouseHold_id().getId() == null) {
            throw new ForbiddenException("El usuario no pertenece a ningún hogar");
        }

        String householdId = user.getHouseHold_id().getId();
        boolean wasOwner = user.getRole() == Role.ROLE_OWNER;

        user.setHouseHold_id(null);
        user.setRole(Role.ROLE_MEMBER);
        userRepository.save(user);

        List<User> remaining = userRepository.findByHouseholdId(householdId);
        if (remaining.isEmpty()) {
            deleteHouseholdAndRelatedData(householdId);
            return;
        }
        if (wasOwner) {
            int idx = ThreadLocalRandom.current().nextInt(remaining.size());
            User newOwner = remaining.get(idx);
            newOwner.setRole(Role.ROLE_OWNER);
            userRepository.save(newOwner);
        }
    }

    /**
     * Elimina neveras (e ítems en cascada), electrodomésticos del hogar y el registro del hogar.
     * No debe quedar ningún usuario referenciando este hogar.
     */
    private void deleteHouseholdAndRelatedData(String householdId) {
        applianceRepository.deleteAllByHouseholdId(householdId);
        fridgeRepository.findByHouseholdId(householdId).forEach(f -> fridgeRepository.deleteById(f.getId()));
        repository.deleteById(householdId);
    }
}