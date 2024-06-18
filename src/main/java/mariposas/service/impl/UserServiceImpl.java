package mariposas.service.impl;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.security.token.render.BearerAccessRefreshToken;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mariposas.exception.BaseException;
import mariposas.model.LoginModel;
import mariposas.model.LoginResponseModel;
import mariposas.model.MenteeProfileModel;
import mariposas.model.MenteesEntity;
import mariposas.model.MentorProfileModel;
import mariposas.model.MentorsEntity;
import mariposas.model.MentorshipEntity;
import mariposas.model.PasswordModel;
import mariposas.model.ResponseModel;
import mariposas.model.UserEntity;
import mariposas.model.UserModel;
import mariposas.model.UserProfileModel;
import mariposas.repository.MenteesRepository;
import mariposas.repository.MentorsRepository;
import mariposas.repository.MentorshipRepository;
import mariposas.repository.UserRepository;
import mariposas.service.AwsCognitoService;
import mariposas.service.EmailService;
import mariposas.service.JwtService;
import mariposas.service.S3Service;
import mariposas.service.UserService;

import java.math.BigDecimal;
import java.util.Objects;

import static mariposas.constant.AppConstant.CHANGE_PASSWORD_SUCCESS;
import static mariposas.constant.AppConstant.FORGOT_PASSWORD;
import static mariposas.constant.AppConstant.IMAGEM_UPLOAD_SUCCESS;
import static mariposas.constant.AppConstant.LOGIN_SUCCESS;
import static mariposas.constant.AppConstant.LOGOUT_SUCCESS;
import static mariposas.constant.AppConstant.PROFILE_DELETE_SUCCESS;
import static mariposas.constant.AppConstant.PROFILE_SUCCESS;
import static mariposas.constant.AppConstant.USER_ALREADY_EXISTS;
import static mariposas.constant.AppConstant.USER_CREATED;
import static mariposas.constant.AppConstant.USER_NOT_FOUND;

@Singleton
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MentorsRepository mentorsRepository;
    private final MenteesRepository menteesRepository;
    private final MentorshipRepository mentorshipRepository;
    private final AwsCognitoService awsCognitoService;
    private final JwtService jwtService;
    private final S3Service s3Service;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository,
                           MentorsRepository mentorsRepository,
                           MenteesRepository menteesRepository,
                           MentorshipRepository mentorshipRepository,
                           AwsCognitoService awsCognitoService,
                           JwtService jwtService,
                           S3Service s3Service,
                           EmailService emailService) {

        this.userRepository = userRepository;
        this.mentorsRepository = mentorsRepository;
        this.menteesRepository = menteesRepository;
        this.mentorshipRepository = mentorshipRepository;
        this.awsCognitoService = awsCognitoService;
        this.jwtService = jwtService;
        this.s3Service = s3Service;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public ResponseModel createUser(UserModel userRequest) {
        try {
            var existingUser = userRepository.findByEmail(userRequest.getEmail());

            if (existingUser != null) {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USER_ALREADY_EXISTS);
            }

            awsCognitoService.registerUser(userRequest.getEmail(), userRequest.getPassword());

            var user = UserEntity.builder()
                    .name(userRequest.getName())
                    .email(userRequest.getEmail())
                    .phone(userRequest.getPhone())
                    .isMentor(userRequest.getIsMentor().getValue())
                    .build();

            var createdUser = userRepository.save(user);

            if (userRequest.getIsMentor().getValue().equals(new BigDecimal(1))) {
                var mentor = MentorsEntity.builder()
                        .userId(createdUser)
                        .mentoringCapacity(new BigDecimal(1))
                        .build();

                mentorsRepository.save(mentor);
            }

            if (userRequest.getIsMentor().getValue().equals(new BigDecimal(2))) {
                var mentee = MenteesEntity.builder()
                        .userId(createdUser)
                        .isSponsored(false)
                        .build();

                menteesRepository.save(mentee);
            }

            emailService.confirmEmail(userRequest.getEmail());

            return buildResponse(USER_CREATED);

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Transactional
    @Override
    public LoginResponseModel login(LoginModel loginRequest) {
        try {
            var existingUser = userRepository.findByEmail(loginRequest.getEmail());

            if (existingUser == null) {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USER_NOT_FOUND);
            }

            var response = awsCognitoService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            var jwtToken = response.authenticationResult().idToken();
            var token = new BearerAccessRefreshToken(jwtToken, null, null, null, null, null);

            return buildLoginResponse(token.getUsername());

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Transactional
    @Override
    public ResponseModel menteeProfile(String email, MenteeProfileModel menteeProfileModel) {
        try {
            var existingUser = userRepository.findByEmail(email);

            if (existingUser != null) {
                var user = menteesRepository.findByUserId(existingUser);
                if (user != null) {
                    var data = MenteesEntity.builder()
                            .id(user.getId())
                            .userId(existingUser)
                            .menteeLevelId(menteeProfileModel.getMenteeLevel() != null ? menteeProfileModel.getMenteeLevel().getValue() : user.getMenteeLevelId())
                            .build();

                    menteesRepository.update(data);

                    var profile = UserEntity.builder()
                            .id(existingUser.getId())
                            .profile(menteeProfileModel.getProfile() != null ? menteeProfileModel.getProfile() : existingUser.getProfile())
                            .age(menteeProfileModel.getAge() != null ? menteeProfileModel.getAge() : existingUser.getAge())
                            .name(existingUser.getName())
                            .email(existingUser.getEmail())
                            .phone(existingUser.getPhone())
                            .isMentor(existingUser.getIsMentor())
                            .build();

                    userRepository.update(profile);
                }

                return buildResponse(PROFILE_SUCCESS);

            } else {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USER_NOT_FOUND);
            }

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Transactional
    @Override
    public ResponseModel mentorProfile(String email, MentorProfileModel mentorProfileModel) {
        try {
            var existingUser = userRepository.findByEmail(email);

            if (existingUser != null) {
                var user = mentorsRepository.findByUserId(existingUser);
                if (user != null) {
                    var data = MentorsEntity.builder()
                            .id(user.getId())
                            .userId(existingUser)
                            .education(mentorProfileModel.getEducation() != null ? mentorProfileModel.getEducation() : user.getEducation())
                            .mentoringCapacity(mentorProfileModel.getMentoringCapacity() != null ? mentorProfileModel.getMentoringCapacity().getValue() : user.getMentoringCapacity())
                            .build();

                    mentorsRepository.update(data);

                    var profile = UserEntity.builder()
                            .id(existingUser.getId())
                            .profile(mentorProfileModel.getProfile() != null ? mentorProfileModel.getProfile() : existingUser.getProfile())
                            .age(mentorProfileModel.getAge() != null ? mentorProfileModel.getAge() : existingUser.getAge())
                            .name(existingUser.getName())
                            .email(existingUser.getEmail())
                            .phone(existingUser.getPhone())
                            .image(existingUser.getImage())
                            .isMentor(existingUser.getIsMentor())
                            .build();

                    userRepository.update(profile);
                }

                return buildResponse(PROFILE_SUCCESS);

            } else {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USER_NOT_FOUND);
            }

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Transactional
    @Override
    public ResponseModel changePassword(PasswordModel passwordModel) {
        try {
            awsCognitoService.changePassword(passwordModel.getEmail(), passwordModel.getPassword());
            return buildResponse(CHANGE_PASSWORD_SUCCESS);
        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Transactional
    @Override
    public ResponseModel deleteUser(String email) {
        try {
            var existingUser = userRepository.findByEmail(email);

            if (existingUser != null) {

                if (existingUser.getIsMentor().equals(new BigDecimal(1))) {
                    var user = mentorsRepository.findByUserId(existingUser);
                    var mentorshipList = mentorshipRepository.findByMentorId(user);

                    if (mentorshipList != null) {
                        for (MentorshipEntity mentorship : mentorshipList) {
                            mentorshipRepository.delete(mentorship);
                        }
                    }

                    mentorsRepository.delete(user);

                } else {
                    var user = menteesRepository.findByUserId(existingUser);
                    var mentorship = mentorshipRepository.findByMenteeId(user);

                    if (mentorship != null) {
                        mentorshipRepository.delete(mentorship);
                    }

                    menteesRepository.delete(user);
                }

                userRepository.delete(existingUser);
                awsCognitoService.deleteUser(email);
            }

            return buildResponse(PROFILE_DELETE_SUCCESS);

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Transactional
    @Override
    public ResponseModel logout(String token) {
        try {
            jwtService.invalidateToken(token);
            return buildResponse(LOGOUT_SUCCESS);

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Transactional
    @Override
    public ResponseModel imageProfile(String email, CompletedFileUpload arquivo) {
        try {
            var name = email.split("@");
            var path = s3Service.uploadFile(name[0], arquivo);

            var existingUser = userRepository.findByEmail(email);

            if (existingUser != null) {
                var user = UserEntity.builder()
                        .id(existingUser.getId())
                        .profile(existingUser.getProfile())
                        .age(existingUser.getAge())
                        .name(existingUser.getName())
                        .email(existingUser.getEmail())
                        .phone(existingUser.getPhone())
                        .isMentor(existingUser.getIsMentor())
                        .image(path)
                        .build();

                userRepository.update(user);
            }

            return buildResponse(IMAGEM_UPLOAD_SUCCESS);

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Transactional
    @Override
    public UserProfileModel userProfile(String email) {
        try {
            var existingUser = userRepository.findByEmail(email);
            var userProfile = new UserProfileModel();

            if (existingUser != null) {

                if (existingUser.getIsMentor().equals(new BigDecimal(1))) {
                    var mentor = mentorsRepository.findByUserId(existingUser);
                    var mentorship = mentorshipRepository.findByMentorId(mentor);

                    userProfile.setName(existingUser.getName());

                    if (existingUser.getImage() != null) {
                        var filename = existingUser.getImage();
                        var imageBytes = s3Service.getImageFile(filename);
                        userProfile.setImage(imageBytes);
                    }

                    userProfile.setEmail(existingUser.getEmail());
                    userProfile.setPhone(existingUser.getPhone());
                    userProfile.setProfile(existingUser.getProfile());
                    userProfile.setAge(existingUser.getAge());
                    userProfile.setEducation(mentor.getEducation());
                    userProfile.setMenteeLevel(null);
                    userProfile.setIsSponsored(null);
                    userProfile.setMentoringCapacity(mentor.getMentoringCapacity().toString());
                    userProfile.setMentoringAvailable(String.valueOf((Integer.parseInt(mentor.getMentoringCapacity().toString()) - mentorship.size())));

                } else {
                    var mentee = menteesRepository.findByUserId(existingUser);

                    userProfile.setName(existingUser.getName());

                    if (existingUser.getImage() != null) {
                        var filename = existingUser.getImage();
                        var imageBytes = s3Service.getImageFile(filename);
                        userProfile.setImage(imageBytes);
                    }

                    userProfile.setEmail(existingUser.getEmail());
                    userProfile.setPhone(existingUser.getPhone());
                    userProfile.setProfile(existingUser.getProfile());
                    userProfile.setAge(existingUser.getAge());
                    userProfile.setEducation(null);
                    var menteeLevel = Objects.equals(mentee.getMenteeLevelId(), new BigDecimal(1)) ? "CASULO" : "LAGARTA";
                    userProfile.setMenteeLevel(menteeLevel);
                    userProfile.setIsSponsored(mentee.getIsSponsored());
                    userProfile.setMentoringCapacity(null);
                    userProfile.setMentoringAvailable(null);
                }
            }

            return userProfile;

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Override
    public ResponseModel forgotPassword(String email) {
        try {
            emailService.forgotPasswordEmail(email);
            return buildResponse(FORGOT_PASSWORD);
        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    private ResponseModel buildResponse(String message) {
        var response = new ResponseModel();
        response.setMensagem(message);
        response.setStatus(BigDecimal.valueOf(HttpStatus.CREATED.getCode()));
        return response;
    }

    private LoginResponseModel buildLoginResponse(String token) {
        var loginResponse = new LoginResponseModel();
        loginResponse.setMensagem(LOGIN_SUCCESS);
        loginResponse.setStatus(BigDecimal.valueOf(HttpStatus.OK.getCode()));
        loginResponse.setToken(token);
        return loginResponse;
    }
}