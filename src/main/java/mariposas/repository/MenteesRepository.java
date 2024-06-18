package mariposas.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.annotation.Nullable;
import mariposas.model.MenteesEntity;
import mariposas.model.MenteesModel;
import mariposas.model.UserEntity;

import static mariposas.constant.AppConstant.QUERY_COUNT_MENTEES;
import static mariposas.constant.AppConstant.QUERY_COUNT_MENTEES_FOR_MENTOR;
import static mariposas.constant.AppConstant.QUERY_GET_MENTEES;
import static mariposas.constant.AppConstant.QUERY_GET_MENTEES_FOR_MENTOR;

@Repository
public interface MenteesRepository extends JpaRepository<MenteesEntity, Long> {
    @Nullable
    MenteesEntity findByUserId(UserEntity user);

    @Query(value = QUERY_GET_MENTEES, nativeQuery = true, countQuery = QUERY_COUNT_MENTEES)
    Page<MenteesModel> findAllMentees(Pageable pageable);

    @Query(value = QUERY_GET_MENTEES_FOR_MENTOR, nativeQuery = true, countQuery = QUERY_COUNT_MENTEES_FOR_MENTOR)
    Page<MenteesModel> findMenteesForMentor(Long id, Pageable pageable);
}