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

@Service
/**
 * Servicio de aplicación para gestionar hogares, miembros y electrodomésticos.
 */
public class HouseHoldServiceImpl implements IHouseHoldService {
    private static final String INVITE_TOKEN_PREFIX = "invite_";
    private static final String INVITE_TOKEN_SEPARATOR = "_";

    private final HouseHoldRepository repository;
    private final UserRepository userRepository;
    private final HouseholdApplianceRepository applianceRepository;
    private final FridgeRepository fridgeRepository;
    /**
     * Crea el servicio con los repositorios necesarios del contexto de hogar.
     * @param repository repositorio de hogares
     * @param userRepository repositorio de usuarios
     * @param applianceRepository repositorio de electrodomésticos del hogar
     * @param fridgeRepository repositorio de neveras
     */

    public HouseHoldServiceImpl(HouseHoldRepository repository, UserRepository userRepository,
            HouseholdApplianceRepository applianceRepository, FridgeRepository fridgeRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.applianceRepository = applianceRepository;
        this.fridgeRepository = fridgeRepository;
    }
    /**
     * Devuelve todos los hogares registrados.
     * @return listado completo de hogares
     */

    @Override
    public List<HouseHold> findAll() {
        return repository.findAll();
    }
    /**
     * Busca un hogar por su identificador.
     * @param id identificador del hogar
     * @return hogar encontrado
     * @throws NotFoundException si no existe el hogar
     */

    @Override
    public HouseHold findById(String id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Unidad Familiar no encontrada"));
    }

    private void ensureHouseholdExists(String householdId) {
        if (!repository.existsById(householdId)) {
            throw new NotFoundException("Unidad Familiar no encontrada");
        }
    }
    /**
     * Crea un hogar y su nevera inicial.
     * @param houseHold datos del hogar a crear
     * @return hogar persistido
     */

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
    /**
     * Elimina un hogar y desvincula a sus miembros.
     * @param id identificador del hogar
     */

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
    /**
     * Quita a un miembro de un hogar.
     * @param householdId identificador del hogar
     * @param memberUserId identificador del miembro a eliminar
     */

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
    /**
     * Promueve a propietario a un usuario del hogar.
     * @param householdId identificador del hogar
     * @param userId identificador del usuario a promover
     * @return usuario actualizado
     */

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
    /**
     * Añade un usuario a un hogar a partir de un token de invitación.
     * @param token token de invitación
     * @param userId identificador del usuario que se une
     * @return usuario añadido al hogar
     */

    @Override
    public User addMemberByToken(String token, String userId) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token de invitacion es obligatorio");
        }
        if (!token.startsWith(INVITE_TOKEN_PREFIX)) {
            throw new IllegalArgumentException("El token de invitacion no tiene un formato valido");
        }

        int separatorIndex = token.lastIndexOf(INVITE_TOKEN_SEPARATOR);
        if (separatorIndex < 0 || separatorIndex == token.length() - 1) {
            throw new IllegalArgumentException("El token de invitacion no contiene un hogar valido");
        }

        String householdId = token.substring(separatorIndex + 1);
        if (householdId.isBlank()) {
            throw new IllegalArgumentException("El token de invitacion no contiene un hogar valido");
        }

        return addMember(householdId, userId);
    }
    /**
     * Añade un electrodoméstico a un hogar.
     * @param householdId identificador del hogar
     * @param appliance tipo de electrodoméstico
     * @return registro de electrodoméstico creado
     */

    @Override
    public HouseholdAppliance addAppliance(String householdId, Appliance appliance) {
        ensureHouseholdExists(householdId);
        if (applianceTypeExistsInHousehold(householdId, appliance, null)) {
            throw new IllegalArgumentException("Ese tipo de electrodomestico ya esta en el hogar");
        }
        HouseholdAppliance newAppliance = new HouseholdAppliance();
        newAppliance.setAppliance(appliance);
        newAppliance.setHouseholdId(householdId);
        return applianceRepository.save(newAppliance);
    }
    /**
     * Añade varios electrodomésticos evitando duplicados por tipo.
     * @param householdId identificador del hogar
     * @param appliances tipos de electrodoméstico a añadir
     * @return listado actualizado de electrodomésticos del hogar
     */

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
    /**
     * Elimina varios electrodomésticos de un hogar por identificador de registro.
     * @param householdId identificador del hogar
     * @param applianceRecordIds identificadores de registros de electrodoméstico
     */

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
                    .orElseThrow(() -> new NotFoundException("ElectrodomAAaAaAaaAAaAAasAAstico no encontrado"));
            if (!ha.getHouseholdId().equals(householdId)) {
                throw new ForbiddenException("El electrodomestico no pertenece a este hogar");
            }
            applianceRepository.deleteById(rid);
        }
    }
    /**
     * Reemplaza la colección completa de electrodomésticos de un hogar.
     * @param householdId identificador del hogar
     * @param appliances nueva lista de tipos de electrodoméstico
     * @return electrodomésticos guardados tras el reemplazo
     */

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
    /**
     * Define el tipo de un electrodoméstico del hogar.
     * @param householdId identificador del hogar
     * @param applianceRecordId identificador del registro de electrodoméstico
     * @param appliance nuevo tipo de electrodoméstico
     * @return registro actualizado
     */

    @Override
    @Transactional
    public HouseholdAppliance updateAppliance(String householdId, String applianceRecordId, Appliance appliance) {
        ensureHouseholdExists(householdId);
        HouseholdAppliance ha = applianceRepository.findById(applianceRecordId)
                .orElseThrow(() -> new NotFoundException("ElectrodomAAaAaAaaAAaAAasAAstico no encontrado"));
        if (!ha.getHouseholdId().equals(householdId)) {
            throw new ForbiddenException("El electrodomestico no pertenece a este hogar");
        }
        if (applianceTypeExistsInHousehold(householdId, appliance, applianceRecordId)) {
            throw new IllegalArgumentException("Ese tipo de electrodomestico ya esta en el hogar");
        }
        ha.setAppliance(appliance);
        return applianceRepository.save(ha);
    }

    private boolean applianceTypeExistsInHousehold(String householdId, Appliance type, String excludeRecordId) {
        return listAppliances(householdId).stream()
                .anyMatch(a -> a.getAppliance() == type
                        && (excludeRecordId == null || !excludeRecordId.equals(a.getId())));
    }
    /**
     * Elimina un electrodoméstico concreto de un hogar.
     * @param householdId identificador del hogar
     * @param applianceRecordId identificador del registro de electrodoméstico
     */

    @Override
    @Transactional
    public void removeApplianceFromHousehold(String householdId, String applianceRecordId) {
        ensureHouseholdExists(householdId);
        HouseholdAppliance ha = applianceRepository.findById(applianceRecordId)
                .orElseThrow(() -> new NotFoundException("ElectrodomAAaAaAaaAAaAAasAAstico no encontrado"));
        if (!ha.getHouseholdId().equals(householdId)) {
            throw new ForbiddenException("El electrodomestico no pertenece a este hogar");
        }
        applianceRepository.deleteById(applianceRecordId);
    }
    /**
     * Lista los electrodomésticos asociados a un hogar.
     * @param householdId identificador del hogar
     * @return lista de electrodomésticos del hogar
     */

    @Override
    public List<HouseholdAppliance> listAppliances(String householdId) {
        return applianceRepository.findByHouseholdId(householdId);
    }
    /**
     * Lista los miembros de un hogar.
     * @param householdId identificador del hogar
     * @return usuarios pertenecientes al hogar
     */

    @Override
    public List<User> listMembers(String householdId) {
        return userRepository.findByHouseholdId(householdId);
    }
    /**
     * Genera un token de invitación para un hogar.
     * @param householdId identificador del hogar
     * @return token de invitación generado
     */

    @Override
    public String generateInviteToken(String householdId) {
        ensureHouseholdExists(householdId);
        return INVITE_TOKEN_PREFIX + UUID.randomUUID() + INVITE_TOKEN_SEPARATOR + householdId;
    }
    /**
     * Añade un usuario como miembro de un hogar.
     * @param householdId identificador del hogar
     * @param userId identificador del usuario
     * @return usuario actualizado
     */

    @Override
    public User addMember(String householdId, String userId) {
        HouseHold houseHold = findById(householdId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        user.setHouseHold_id(houseHold);
        user.setRole(Role.ROLE_MEMBER);
        return userRepository.save(user);
    }
    /**
     * Gestiona la salida de un usuario de su hogar actual.
     * @param userId identificador del usuario que abandona el hogar
     */

    @Override
    @Transactional
    public void leaveHousehold(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (user.getHouseHold_id() == null || user.getHouseHold_id().getId() == null) {
            throw new ForbiddenException("El usuario no pertenece a ningun hogar");
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

    private void deleteHouseholdAndRelatedData(String householdId) {
        applianceRepository.deleteAllByHouseholdId(householdId);
        fridgeRepository.findByHouseholdId(householdId).forEach(f -> fridgeRepository.deleteById(f.getId()));
        repository.deleteById(householdId);
    }
}




