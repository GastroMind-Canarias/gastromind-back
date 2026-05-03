package com.gastromind.api.application.services;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.ports.in.IAllergenService;
import com.gastromind.api.domain.ports.out.AllergenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio encargado de gestionar el catalogo de alergenos alimentarios.
 * Proporciona la logica necesaria para el mantenimiento y consulta de sustancias 
 * que requieren especial atenciAn en el sistema.
 */
@Service
public class AllergenServiceImpl implements IAllergenService {

    private final AllergenRepository repository;

    /**
     * Constructor para la inyecciAn del repositorio de persistencia.
     * @param repository Repositorio de datos para el acceso a la base de datos de alergenos.
     */
    public AllergenServiceImpl(AllergenRepository repository) {
        this.repository = repository;
    }

    /**
     * Recupera todos los alergenos registrados en el sistema.
     * @return Una lista con la totalidad de alergenos disponibles.
     */
    @Override
    public List<Allergen> findAll() {
        return repository.findAll();
    }

    /**
     * Localiza la ficha de un alergeno especifico mediante su identificador.
     * @param id El UUID o clave primaria del alergeno.
     * @return El objeto Allergen con la informacion solicitada.
     * @throws NotFoundException Si no existe ningun alergeno con el ID proporcionado.
     */
    @Override
    public Allergen findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("AlArgeno no encontrado"));
    }

    /**
     * Registra una nueva sustancia alArgena en la base de datos.
     * @param allergen Objeto con la informacion del alergeno a dar de alta.
     * @return El alergeno una vez guardado y con su ID generado.
     */
    @Override
    @Transactional
    public Allergen create(Allergen allergen) {
        return repository.save(allergen);
    }

    /**
     * Define los datos de un alergeno ya existente.
     * @param id Identificador del alergeno que se desea modificar.
     * @param allergen Objeto con los nuevos datos a persistir.
     * @return La entidad actualizada tras confirmar su existencia previa.
     */
    @Override
    @Transactional
    public Allergen update(String id, Allergen allergen) {
        findById(id); // ValidaciAn de existencia
        allergen.setId(id);
        return repository.save(allergen);
    }

    /**
     * Elimina de forma definitiva un alergeno del catalogo.
     * @param id Identificador Anico de la sustancia a dar de baja.
     */
    @Override
    @Transactional
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
}