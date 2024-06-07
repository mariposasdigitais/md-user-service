package mariposas.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import jakarta.annotation.Nullable;
import mariposas.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Nullable
    UserEntity findByEmail(String email);
}