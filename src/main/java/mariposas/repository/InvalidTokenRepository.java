package mariposas.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import jakarta.annotation.Nullable;
import mariposas.model.InvalidTokenEntity;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidTokenEntity, Long> {
    @Nullable
    InvalidTokenEntity findByToken(String token);
}