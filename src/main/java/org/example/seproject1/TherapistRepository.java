package org.example.seproject1;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TherapistRepository extends MongoRepository<Therapist, String> {
    Optional<Therapist> findByEmail(String email);
    Optional<Therapist> findByUsername(String username);
    List<Therapist> findAll();
}
