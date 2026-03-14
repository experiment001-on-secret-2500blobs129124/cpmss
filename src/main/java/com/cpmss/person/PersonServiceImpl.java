package com.cpmss.person;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Override
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Override
    public Optional<Person> findById(UUID id) {
        return personRepository.findById(id);
    }

    @Override
    public Optional<Person> findByNationalId(String nationalId) {
        return personRepository.findByNationalId(nationalId);
    }

    @Override
    public List<Person> findByType(String personType) {
        return personRepository.findByPersonType(personType);
    }

    @Override
    public List<Person> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        return personRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query);
    }

    @Override
    @Transactional
    public Person create(Person person) {
        // Enforce default type if not set
        if (person.getPersonType() == null || person.getPersonType().isBlank()) {
            person.setPersonType("Visitor");
        }
        return personRepository.save(person);
    }

    @Override
    @Transactional
    public Person update(UUID id, Person updated) {
        Person existing = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found: " + id));

        existing.setNationalId(updated.getNationalId());
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setNationality(updated.getNationality());
        existing.setPhone1Country(updated.getPhone1Country());
        existing.setPhone1Number(updated.getPhone1Number());
        existing.setPhone2Country(updated.getPhone2Country());
        existing.setPhone2Number(updated.getPhone2Number());
        existing.setEmail1(updated.getEmail1());
        existing.setEmail2(updated.getEmail2());
        existing.setCity(updated.getCity());
        existing.setStreet(updated.getStreet());
        existing.setDateOfBirth(updated.getDateOfBirth());
        existing.setGender(updated.getGender());
        existing.setPersonType(updated.getPersonType());
        existing.setQualification(updated.getQualification());
        existing.setIsBlacklisted(updated.getIsBlacklisted());

        return personRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!personRepository.existsById(id)) {
            throw new EntityNotFoundException("Person not found: " + id);
        }
        personRepository.deleteById(id);
    }

    @Override
    public long count() {
        return personRepository.count();
    }
}
