package com.example.backend.register;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailAndSocial(String email, String social);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);

    
}
