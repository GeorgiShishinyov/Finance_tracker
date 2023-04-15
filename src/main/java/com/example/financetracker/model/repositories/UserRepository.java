package com.example.financetracker.model.repositories;

import com.example.financetracker.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    boolean existsByUniqueCode(String code);
    Optional<User> findByUniqueCode(String code);

    List<User> findByIsVerifiedAndExpirationDateBefore(Boolean isVerified, LocalDateTime localDateTime);

    Optional<User> findByUniqueCodeAndExpirationDateBefore(String code, LocalDateTime localDateTime);

    @Query("SELECT u FROM users AS u JOIN u.accounts a WHERE u.id = ?1 AND a.id = ?2")
    User findUserByIdAndAccountId(int id, int accountId);
}
