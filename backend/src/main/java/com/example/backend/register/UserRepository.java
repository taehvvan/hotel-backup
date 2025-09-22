package com.example.backend.register;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmailAndSocial(String email, String social);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    // 리프레시 토큰으로 사용자 조회
    Optional<UserEntity> findByRefreshToken(String refreshToken);

}
