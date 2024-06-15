package mariposas.service.impl;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mariposas.exception.BaseException;
import mariposas.model.MenteesModel;
import mariposas.model.MentorModel;
import mariposas.model.MentorshipEntity;
import mariposas.model.PaginatedMentees;
import mariposas.model.ResponseModel;
import mariposas.model.SponsorshipModel;
import mariposas.repository.MenteesRepository;
import mariposas.repository.MentorsRepository;
import mariposas.repository.MentorshipRepository;
import mariposas.repository.UserRepository;
import mariposas.service.S3Service;
import mariposas.service.SponsorshipService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static mariposas.constant.AppConstant.GET_MENTOR_ERROR;
import static mariposas.constant.AppConstant.SPONSORSHIP_CANCEL_ERROR;
import static mariposas.constant.AppConstant.SPONSORSHIP_CANCEL_SUCCESS;
import static mariposas.constant.AppConstant.SPONSORSHIP_ERROR;
import static mariposas.constant.AppConstant.SPONSORSHIP_SUCCESS;
import static mariposas.constant.AppConstant.USERS_NOT_FOUND;
import static mariposas.constant.AppConstant.USER_NOT_FOUND;

@Singleton
public class SponsorshipServiceImpl implements SponsorshipService {
    private final UserRepository userRepository;
    private final MentorsRepository mentorsRepository;
    private final MenteesRepository menteesRepository;
    private final MentorshipRepository mentorshipRepository;
    private final S3Service s3Service;


    public SponsorshipServiceImpl(UserRepository userRepository,
                                  MentorsRepository mentorsRepository,
                                  MenteesRepository menteesRepository,
                                  MentorshipRepository mentorshipRepository,
                                  S3Service s3Service) {

        this.userRepository = userRepository;
        this.mentorsRepository = mentorsRepository;
        this.menteesRepository = menteesRepository;
        this.mentorshipRepository = mentorshipRepository;
        this.s3Service = s3Service;
    }

    @Override
    public PaginatedMentees getMenteesList(Integer limit, Integer page) {
        Pageable pageable = Pageable.from(page - 1, limit);
        Page<MenteesModel> menteePage = menteesRepository.findAllMentees(pageable);

        List<MenteesModel> listMentees = new ArrayList<>();

        for (MenteesModel menteesModel : menteePage.getContent()) {
            var filename = new String(menteesModel.getImage());
            var imageBytes = s3Service.getImageFile(filename);
            menteesModel.setImage(imageBytes);

            listMentees.add(menteesModel);
        }

        var paginatedMentees = new PaginatedMentees();
        paginatedMentees.data(listMentees);
        paginatedMentees.setCurrentPage(menteePage.getPageNumber() + 1);
        paginatedMentees.totalRecordsPerPage(limit);
        paginatedMentees.setTotalRecords((int) menteePage.getTotalSize());

        return paginatedMentees;
    }

    @Transactional
    @Override
    public ResponseModel sponsoringMentee(SponsorshipModel sponsorshipModel) {
        try {
            var existingMentee = userRepository.findByEmail(sponsorshipModel.getEmailMentee());
            var existingMentor = userRepository.findByEmail(sponsorshipModel.getEmailMentor());

            if (existingMentee == null || existingMentor == null) {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USERS_NOT_FOUND);
            }

            var mentee = menteesRepository.findByUserId(existingMentee);

            if (!mentee.getIsSponsored()) {
                var mentor = mentorsRepository.findByUserId(existingMentor);
                var sponsorship = mentorshipRepository.findByMentorId(mentor);

                var sponsorshipsQtd = new BigDecimal(sponsorship.size());

                if (sponsorship.size() == 0 || sponsorshipsQtd.compareTo(mentor.getMentoringCapacity()) < 0) {
                    var mentorship = MentorshipEntity.builder()
                            .menteeId(mentee)
                            .mentorId(mentor)
                            .build();

                    mentorshipRepository.save(mentorship);

                    mentee.setIsSponsored(true);
                    menteesRepository.update(mentee);
                } else {
                    throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, SPONSORSHIP_ERROR);
                }

            }

            return buildResponse(SPONSORSHIP_SUCCESS);

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, SPONSORSHIP_ERROR);
        }
    }

    @Transactional
    @Override
    public ResponseModel cancelSponsorship(String emailMentee, String emailMentor) {
        try {
            var existingMentee = userRepository.findByEmail(emailMentee);
            var existingMentor = userRepository.findByEmail(emailMentor);

            if (existingMentee == null || existingMentor == null) {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USERS_NOT_FOUND);
            }

            var mentee = menteesRepository.findByUserId(existingMentee);
            mentee.setIsSponsored(false);
            menteesRepository.update(mentee);

            var sponsorship = mentorshipRepository.findByMenteeId(mentee);

            if (sponsorship == null) {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USERS_NOT_FOUND);
            }

            mentorshipRepository.delete(sponsorship);

            return buildResponse(SPONSORSHIP_CANCEL_SUCCESS);

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, SPONSORSHIP_CANCEL_ERROR);
        }
    }

    @Transactional
    @Override
    public MentorModel getMentorProfile(String email) {
        try {
            var existingMentee = userRepository.findByEmail(email);

            if (existingMentee == null) {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USER_NOT_FOUND);
            }

            var mentee = menteesRepository.findByUserId(existingMentee);
            var mentor = mentorsRepository.findMentorByMenteeId(mentee.getId());

            var filename = new String(mentor.getImage());
            var imageBytes = s3Service.getImageFile(filename);
            mentor.setImage(imageBytes);

            return mentor;

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, GET_MENTOR_ERROR);
        }
    }

    private ResponseModel buildResponse(String message) {
        var response = new ResponseModel();
        response.setMensagem(message);
        response.setStatus(BigDecimal.valueOf(HttpStatus.CREATED.getCode()));
        return response;
    }
}