package mariposas.service.impl;

import io.micronaut.http.HttpStatus;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mariposas.exception.BaseException;
import mariposas.model.MenteesModelInner;
import mariposas.model.MentorModelInner;
import mariposas.model.MentorshipEntity;
import mariposas.model.ResponseModel;
import mariposas.model.SponsorshipModel;
import mariposas.model.SponsorshipNotificationModel;
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

    @Transactional
    @Override
    public List<MenteesModelInner> getMenteesList() {
        var mentee = menteesRepository.findAllMentees();

        List<MenteesModelInner> listMentees = new ArrayList<>();

        for (MenteesModelInner menteesModel : mentee) {
            if (menteesModel.getImage() != null) {
                var filename = new String(menteesModel.getImage());
                var imageBytes = s3Service.getImageFile(filename);
                menteesModel.setImage(imageBytes);
            }

            listMentees.add(menteesModel);
        }

        return listMentees;
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
    public List<MentorModelInner> getMentorProfile(String email) {
        try {
            var existingUser = userRepository.findByEmail(email);

            if (existingUser == null) {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USER_NOT_FOUND);
            }

            if (existingUser.getIsMentor().equals(new BigDecimal(2))) {

                var mentee = menteesRepository.findByUserId(existingUser);
                if (mentee != null) {
                    var mentor = mentorsRepository.findMentorByMenteeId(mentee.getId());

                    if (mentor.size() != 0) {
                        if (mentor.get(0).getImage() != null) {
                            var filename = new String(mentor.get(0).getImage());
                            var imageBytes = s3Service.getImageFile(filename);
                            mentor.get(0).setImage(imageBytes);
                        }

                        return mentor;
                    }
                }

                List<MentorModelInner> dataList = new ArrayList<>();
                return dataList;
            }

            var mentor = mentorsRepository.findByUserId(existingUser);
            var listMentee = menteesRepository.findMenteesForMentor(mentor.getId());

            List<MentorModelInner> dataList = new ArrayList<>();

            if (!listMentee.isEmpty()) {
                for (MenteesModelInner menteesModel : listMentee) {
                    var data = new MentorModelInner(); // Cria uma nova instância a cada iteração

                    if (menteesModel.getImage() != null) {
                        var filename = new String(menteesModel.getImage());
                        var imageBytes = s3Service.getImageFile(filename);
                        data.setImage(imageBytes);
                    }

                    data.setProfile(menteesModel.getProfile());
                    data.setAge(menteesModel.getAge());
                    data.setEducation(null);
                    data.setEmail(menteesModel.getEmail());
                    data.setName(menteesModel.getName());
                    data.setMenteeLevel(menteesModel.getMenteeLevel());
                    data.setPhone(menteesModel.getPhone());

                    dataList.add(data);
                }
            }

            return dataList;

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, GET_MENTOR_ERROR);
        }
    }

    @Transactional
    @Override
    public SponsorshipNotificationModel sponsorshipNotification(String email) {
        try {
            var existingUser = userRepository.findByEmail(email);
            var sponsorshipNotification = new SponsorshipNotificationModel();

            if (existingUser == null) {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USER_NOT_FOUND);
            }

            if (existingUser.getIsMentor().equals(new BigDecimal(2))) {

                var mentee = menteesRepository.findByUserId(existingUser);
                var mentorship = mentorshipRepository.findByMenteeId(mentee);


                if (mentorship != null) {
                    var mentor = mentorsRepository.findMentorByMenteeId(mentee.getId());
                    sponsorshipNotification.setEmail(mentor.get(0).getEmail());
                    sponsorshipNotification.setName(mentor.get(0).getName());

                    if (mentor.get(0).getImage() != null) {
                        var filename = new String(mentor.get(0).getImage());
                        var imageBytes = s3Service.getImageFile(filename);
                        sponsorshipNotification.setImage(imageBytes);
                    }

                    sponsorshipNotification.setPhone(mentor.get(0).getPhone());
                    sponsorshipNotification.setProfile(mentor.get(0).getProfile());
                    sponsorshipNotification.setAge(mentor.get(0).getAge());
                    sponsorshipNotification.setEducation(mentor.get(0).getEducation());
                    sponsorshipNotification.setIsNotification(null);
                }

                return sponsorshipNotification;
            }

            var mentee = menteesRepository.findAllMentees();
            var mentor = mentorsRepository.findByUserId(existingUser);
            var mentorship = mentorshipRepository.findByMentorId(mentor);

            if (mentee == null || mentor == null || mentorship == null) {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USER_NOT_FOUND);
            }

            var mentoringAvaliable = Integer.parseInt(mentor.getMentoringCapacity().toString()) - mentorship.size();

            sponsorshipNotification.setEmail(null);
            sponsorshipNotification.setName(null);
            sponsorshipNotification.setImage(null);
            sponsorshipNotification.setPhone(null);
            sponsorshipNotification.setProfile(null);
            sponsorshipNotification.setAge(null);
            sponsorshipNotification.setEducation(null);

            if (mentee.isEmpty()) {
                sponsorshipNotification.setIsNotification(false);
            }

            if (mentoringAvaliable != 0) {
                sponsorshipNotification.setIsNotification(true);
            }

            return sponsorshipNotification;

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, GET_MENTOR_ERROR);
        }
    }

    @Override
    public List<MenteesModelInner> getMentorMenteesList(String email) {
        var existingUser = userRepository.findByEmail(email);

        if (existingUser == null) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USER_NOT_FOUND);
        }

        var mentor = mentorsRepository.findByUserId(existingUser);
        var listMentee = menteesRepository.findMenteesForMentor(mentor.getId());

        List<MenteesModelInner> listMentees = new ArrayList<>();

        for (MenteesModelInner menteesModel : listMentee) {
            if (menteesModel.getImage() != null) {
                var filename = new String(menteesModel.getImage());
                var imageBytes = s3Service.getImageFile(filename);
                menteesModel.setImage(imageBytes);
            }

            listMentees.add(menteesModel);
        }

        return listMentees;
    }

    private ResponseModel buildResponse(String message) {
        var response = new ResponseModel();
        response.setMensagem(message);
        response.setStatus(BigDecimal.valueOf(HttpStatus.CREATED.getCode()));
        return response;
    }
}