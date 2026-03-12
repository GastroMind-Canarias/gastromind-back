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

import java.util.List;
import java.util.UUID;

@Service
public class HouseHoldServiceImpl implements IHouseHoldService {

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
    public HouseHold update(String id, HouseHold houseHold) {
        findById(id);
        houseHold.setId(id);
        return repository.save(houseHold);
    }

    @Override
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }

    @Override
    public HouseHold createWithCreator(HouseHold houseHold, String creatorUserId) {
        HouseHold savedHouseHold = repository.save(houseHold);
        createFridge(savedHouseHold);
        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new NotFoundException("Usuario creador no encontrado"));
        creator.setHouseHold_id(savedHouseHold);
        creator.setRole(Role.ROLE_OWNER);
        userRepository.save(creator);
        return savedHouseHold;
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
        // Generamos un token simple vinculado al householdId
        return UUID.randomUUID().toString() + "-" + householdId;
    }

    @Override
    public void removeMember(String ownerId, String householdId, String memberUserId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Propietario no encontrado"));
        if (!Role.ROLE_OWNER.equals(owner.getRole()) || owner.getHouseHold_id() == null
                || !owner.getHouseHold_id().getId().equals(householdId)) {
            throw new ForbiddenException("Solo el propietario del hogar puede eliminar miembros");
        }

        User member = userRepository.findById(memberUserId)
                .orElseThrow(() -> new NotFoundException("Miembro no encontrado"));
        if (member.getHouseHold_id() == null || !member.getHouseHold_id().getId().equals(householdId)) {
            throw new NotFoundException("El usuario no pertenece a este hogar");
        }

        member.setHouseHold_id(null);
        member.setRole(Role.ROLE_MEMBER); // Volvemos a un rol por defecto
        userRepository.save(member);
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
}