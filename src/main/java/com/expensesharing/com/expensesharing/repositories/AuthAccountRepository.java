package com.expensesharing.com.expensesharing.repositories;

import com.expensesharing.com.expensesharing.entity.AuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthAccountRepository extends JpaRepository<AuthAccount, Long> {
    Optional<AuthAccount> findByUsername(String username);

    boolean existsByUsername(String username);
}
