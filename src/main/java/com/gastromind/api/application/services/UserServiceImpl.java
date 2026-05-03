package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.models.User;
import com.gastromind.api.domain.models.enums.Role;
import com.gastromind.api.domain.ports.in.IUserService;
import com.gastromind.api.domain.ports.out.AllergenRepository;
import com.gastromind.api.domain.ports.out.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

@Service
/**
 * Servicio de aplicacion para gestionar usuarios y sus alergenos.
 */
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final AllergenRepository allergenRepository;
    /**
     * Crea el servicio con sus repositorios de usuarios y alergenos.
     * @param userRepository repositorio de usuarios
     * @param allergenRepository repositorio de alergenos
     */

    public UserServiceImpl(UserRepository userRepository, AllergenRepository allergenRepository) {
        this.userRepository = userRepository;
        this.allergenRepository = allergenRepository;
    }
    /**
     * Devuelve todos los usuarios registrados.
     * @return listado completo de usuarios
     */

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
    /**
     * Busca un usuario por su identificador.
     * @param id identificador del usuario
     * @return usuario encontrado
     * @throws NotFoundException si el usuario no existe
     */

    @Override
    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }
    /**
     * Busca un usuario por nombre.
     * @param username nombre de usuario
     * @return usuario encontrado
     * @throws NotFoundException si no existe un usuario con ese nombre
     */

    @Override
    public User findByUsername(String username) {
        return userRepository.findByName(username).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }
    /**
     * Registra un nuevo usuario.
     * @param user datos del usuario a crear
     * @return usuario persistido
     */

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }
    /**
     * Define un usuario existente.
     * @param id identificador del usuario
     * @param user nuevos datos del usuario
     * @return usuario actualizado
     */

    @Override
    public User update(String id, User user) {
        findById(id);
        user.setId(id);
        return userRepository.save(user);
    }
    /**
     * Define los datos de perfil de un usuario y su lista de alergenos.
     * @param id identificador del usuario
     * @param userChanges cambios de perfil a aplicar
     * @return usuario actualizado
     */

    @Override
    @Transactional
    public User updateProfile(String id, User userChanges) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        existingUser.setName(userChanges.getName());
        existingUser.setEmail(userChanges.getEmail());

        if (userChanges.getAllergens() != null) {
            existingUser.getAllergens().clear();

            userChanges.getAllergens().forEach(allergenChanges -> {
                Allergen realAllergen = allergenRepository.findById(allergenChanges.getId()).orElseThrow(() -> new NotFoundException("Alergeno no encontrado: " + allergenChanges.getId()));
                existingUser.addAllergen(realAllergen);
            });
        }

        return userRepository.save(existingUser);
    }
    /**
     * Elimina un usuario por su identificador.
     * @param id identificador del usuario
     */

    @Override
    public void delete(String id) {
        findById(id);
        userRepository.deleteById(id);
    }
    /**
     * Asocia un alergeno concreto al usuario.
     * @param userId identificador del usuario
     * @param allergenId identificador del alergeno
     */

    @Override
    @Transactional
    public void addAllergen(String userId, String allergenId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        Allergen allergen = allergenRepository.findById(allergenId).orElseThrow(() -> new NotFoundException("Alergeno no encontrado"));
        user.addAllergen(allergen);
        userRepository.save(user);
    }
    /**
     * Asocia varios alergenos al usuario, evitando duplicados.
     * @param userId identificador del usuario
     * @param allergenIds lista de identificadores de alergeno
     */

    @Override
    @Transactional
    public void addAllergensBulk(String userId, List<String> allergenIds) {
        if (allergenIds == null || allergenIds.isEmpty()) {
            return;
        }
        for (String id : new LinkedHashSet<>(allergenIds)) {
            if (id == null || id.isBlank()) {
                continue;
            }
            addAllergen(userId, id);
        }
    }
    /**
     * Elimina un alergeno de la lista de un usuario.
     * @param userId identificador del usuario
     * @param allergenId identificador del alergeno
     */

    @Override
    @Transactional
    public void removeAllergen(String userId, String allergenId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        user.removeAllergen(allergenId);
        userRepository.save(user);
    }
    /**
     * Elimina varios alergenos de la lista del usuario.
     * @param userId identificador del usuario
     * @param allergenIds lista de identificadores de alergeno
     */

    @Override
    @Transactional
    public void removeAllergensBulk(String userId, List<String> allergenIds) {
        if (allergenIds == null) {
            return;
        }
        for (String id : allergenIds) {
            if (id == null || id.isBlank()) {
                continue;
            }
            removeAllergen(userId, id);
        }
    }
    /**
     * Sustituye la lista completa de alergenos de un usuario.
     * @param userId identificador del usuario
     * @param allergenIds nueva lista de identificadores de alergeno
     */

    @Override
    @Transactional
    public void replaceAllergens(String userId, List<String> allergenIds) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        user.getAllergens().clear();
        userRepository.save(user);
        if (allergenIds == null || allergenIds.isEmpty()) {
            return;
        }
        addAllergensBulk(userId, allergenIds);
    }
    /**
     * Devuelve los alergenos configurados para un usuario.
     * @param userId identificador del usuario
     * @return lista de alergenos asociados
     */

    @Override
    public List<Allergen> listAllergens(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return List.copyOf(user.getAllergens());
    }
    /**
     * Cambia el rol de un usuario.
     * @param id identificador del usuario
     * @param newRole nuevo rol a asignar
     * @return usuario con el rol actualizado
     */

    @Override
    @Transactional
    public User updateUserRole(String id, Role newRole) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

        user.setRole(newRole);

        return userRepository.save(user);
    }
}




