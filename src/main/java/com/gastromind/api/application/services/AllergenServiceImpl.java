package com.gastromind.api.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gastromind.api.domain.exceptions.NotFoundException;
import com.gastromind.api.domain.models.Allergen;
import com.gastromind.api.domain.ports.in.IAllergenService;
import com.gastromind.api.domain.ports.out.AllergenRepository;

import jakarta.transaction.Transactional;

@Service
public class AllergenServiceImpl implements IAllergenService {

    private final AllergenRepository repository;


    public AllergenServiceImpl(AllergenRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Allergen> findAll() {
        return repository.findAll();
    }

    @Override
    public Allergen findById(String id) {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Alérgeno no encontrado"));
    }

    @Override
    @Transactional
    public Allergen create(Allergen allergen) {
        return repository.save(allergen);
    }

    @Override
    @Transactional
    public Allergen update(String id, Allergen allergen) {
        findById(id);
        allergen.setId(id);
        return repository.save(allergen);
    }

    @Override
    @Transactional
    public void delete(String id) {
        findById(id);
        repository.deleteById(id);
    }
} 

