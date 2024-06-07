package mariposas.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import jakarta.annotation.Nullable;
import mariposas.model.MentorsEntity;
import mariposas.model.UserEntity;

@Repository
public interface MentorsRepository extends JpaRepository<MentorsEntity, Long> {
    @Nullable
    MentorsEntity findByUserId(UserEntity user);
}