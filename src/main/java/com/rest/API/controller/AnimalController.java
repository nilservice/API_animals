package com.rest.API.controller;

import com.rest.API.exception.AnimalNotFoundException;
import com.rest.API.model.Animal;
import com.rest.API.repository.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.validation.Valid;
import java.util.List;

@RestController
public class AnimalController {

    @Autowired
    AnimalRepository animalRepository;

    // Получить все записи
    @GetMapping("/animals")
    public List getAllNotes() {
        return animalRepository.findAll();
    }

    // Создать запись
    @PostMapping("/animals")
    public Animal createNote(@Valid @RequestBody Animal animal) {
        return  animalRepository.save(animal);
    }

    // Получить запись по id
    @GetMapping("/animals/{id}")
    public Animal getNoteById(@PathVariable(value = "id") Long animalId) throws AnimalNotFoundException {
        return animalRepository.findById(animalId)
                .orElseThrow(() -> new AnimalNotFoundException(animalId));
    }

    // Получить запись по type


    @GetMapping("/animals/type={type}")
    public List<Animal> getByType(@PathVariable(value = "type") String animalType){
        return  animalRepository.findAllByType(animalType);
    }

    @GetMapping("/animals/name={name}")
    public List<Animal> getByName(@PathVariable(value = "name") String animalName){
        return  animalRepository.findAllByNameContaining(animalName);
    }

    // Обновить запись
    @PutMapping("/animals/{id}")
    public Animal updateNote(@PathVariable(value = "id") Long animalId,
                             @Valid @RequestBody Animal animalDetails) throws AnimalNotFoundException {

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new AnimalNotFoundException(animalId));

        animal.setName(animalDetails.getName());
        animal.setType(animalDetails.getType());
        animal.setPrice(animalDetails.getPrice());

        Animal updatedAnimal = animalRepository.save(animal);
        return updatedAnimal;
    }   // Удалить запись по id
    @DeleteMapping("/animals/{id}")
    public ResponseEntity deleteAnimal(@PathVariable(value = "id") Long animalId) throws AnimalNotFoundException {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new AnimalNotFoundException(animalId));

        animalRepository.delete(animal);
        return ResponseEntity.ok().build();
    }
}