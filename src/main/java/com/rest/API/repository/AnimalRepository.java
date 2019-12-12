package com.rest.API.repository;


import com.rest.API.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

//    Поиск по type

    List <Animal> findAllByType(@Param("type") String type);

//    Поиск по схожести в имени
    List <Animal> findAllByNameContaining(@Param("name") String type);

}