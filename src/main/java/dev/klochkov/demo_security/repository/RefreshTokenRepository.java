package dev.klochkov.demo_security.repository;

import dev.klochkov.demo_security.entity.RefreshToken;
import dev.klochkov.demo_security.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @EntityGraph(attributePaths = "user")
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
}
