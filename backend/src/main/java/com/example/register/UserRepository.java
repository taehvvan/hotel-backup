package com.example.register;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDTO, Integer> {
    UserEntity findByEmail(String email);

}
