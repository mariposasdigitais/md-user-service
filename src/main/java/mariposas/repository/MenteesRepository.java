package mariposas.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import jakarta.annotation.Nullable;
import mariposas.model.MenteesEntity;
import mariposas.model.UserEntity;

@Repository
public interface MenteesRepository extends JpaRepository<MenteesEntity, Long> {
    @Nullable
    MenteesEntity findByUserId(UserEntity user);
}