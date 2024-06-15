package mariposas.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import jakarta.annotation.Nullable;
import mariposas.model.MenteesEntity;
import mariposas.model.MentorsEntity;
import mariposas.model.MentorshipEntity;

import java.util.List;

@Repository
public interface MentorshipRepository extends JpaRepository<MentorshipEntity, Long> {
    @Nullable
    List<MentorshipEntity> findByMentorId(MentorsEntity mentorId);

    @Nullable
    MentorshipEntity findByMenteeId(MenteesEntity menteeId);
}