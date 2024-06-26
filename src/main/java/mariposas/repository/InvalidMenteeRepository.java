package mariposas.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import jakarta.annotation.Nullable;
import mariposas.model.InvalidMenteeEntity;
import mariposas.model.InvalidMenteeModelInner;
import mariposas.model.MentorsEntity;

import java.util.List;

import static mariposas.constant.AppConstant.QUERY_INVALID_MENTEES;

@Repository
public interface InvalidMenteeRepository extends JpaRepository<InvalidMenteeEntity, Long> {
    @Query(value = QUERY_INVALID_MENTEES, nativeQuery = true)
    List<InvalidMenteeModelInner> findByMentorId(Long id);
}