package com.github.syakimovich.chessserver.repositories;

import com.github.syakimovich.chessserver.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    default User findByUsernameOrThrowException(String username) {
        return findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username %s not found".formatted(username)));
    }
}
