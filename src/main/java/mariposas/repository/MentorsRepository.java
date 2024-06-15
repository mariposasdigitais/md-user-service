package mariposas.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import jakarta.annotation.Nullable;
import mariposas.model.MentorModel;
import mariposas.model.MentorsEntity;
import mariposas.model.UserEntity;

import static mariposas.constant.AppConstant.QUERY_GET_MENTOR;

@Repository
public interface MentorsRepository extends JpaRepository<MentorsEntity, Long> {
    @Nullable
    MentorsEntity findByUserId(UserEntity user);

    @Query(value = QUERY_GET_MENTOR, nativeQuery = true)
    MentorModel findMentorByMenteeId(Long id);
}