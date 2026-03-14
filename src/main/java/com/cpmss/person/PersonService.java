package com.cpmss.person;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Person entity operations.
 * Defines the contract — implementations can be swapped for testing or alternative logic.
 */
public interface PersonService {

    List<Person> findAll();

    Optional<Person> findById(UUID id);

    Optional<Person> findByNationalId(String nationalId);

    List<Person> findByType(String personType);

    List<Person> search(String query);

    Person create(Person person);

    Person update(UUID id, Person person);

    void delete(UUID id);

    long count();
}
