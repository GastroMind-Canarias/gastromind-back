package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.ports.in.IAllergenService;
import com.gastromind.api.domain.ports.out.AllergenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio encargado de gestionar el catálogo de alérgenos alimentarios.
 * Proporciona la lógica necesaria para el mantenimiento y consulta de sustancias 
 * que requieren especial atención en el sistema.
 */
@Service
public class AllergenServiceImpl implements IAllergenService {

    private final AllergenRepository repository;

    /**
     * Constructor para la inyección del repositorio de persistencia.
     * @param repository Repositorio de datos para el acceso a la base de datos de alérgenos.
     */
    public AllergenServiceImpl(AllergenRepository repository) {
        this.repository = repository;
    }

    /**
     * Recupera todos los alérgenos registrados en el sistema.
     * @return Una lista con la totalidad de alérgenos disponibles.
     */
    @Override
    public List<Allergen> findAll() {
        return repository.findAll();
    }

    /**
     * Localiza la ficha de un alérgeno específico mediante su identificador.
     * @param id El UUID o clave primaria del alérgeno.
     * @return El objeto Allergen con la información solicitada.
     * @throws NotFoundException Si no existe ningún alérgeno con el ID proporcionado.
     */
    @Override
    public Allergen findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Alérgeno no encontrado"));
    }

    /**
     * Registra una nueva sustancia alérgena en la base de datos.
     * @param allergen Objeto con la información del alérgeno a dar de alta.
     * @return El alérgeno una vez guardado y con su ID generado.
     */
    @Override
    @Transactional
    public Allergen create(Allergen allergen) {
        return repository.save(allergen);
    }

    /**
     * Define los datos de un alérgeno ya existente.
     * @param id Identificador del alérgeno que se desea modificar.
     * @param allergen Objeto con los nuevos datos a persistir.
     * @return La entidad actualizada tras confirmar su existencia previa.
     */
    @Override
    @Transactional
    public Allergen update(String id, Allergen allergen) {
        findById(id); // Validación de existencia
        allergen.setId(id);
        return repository.save(allergen);
    }

    /**
     * Elimina de forma definitiva un alérgeno del catálogo.
     * @param id Identificador único de la sustancia a dar de baja.
     */
    @Override
    @Transactional
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}