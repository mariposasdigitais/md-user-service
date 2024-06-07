package mariposas.service.impl;

import io.micronaut.http.HttpStatus;
import io.micronaut.security.token.render.BearerAccessRefreshToken;
import jakarta.inject.Singleton;
import mariposas.exception.BaseException;
import mariposas.model.LoginModel;
import mariposas.model.LoginResponseModel;
import mariposas.model.MenteeProfileModel;
import mariposas.model.MenteesEntity;
import mariposas.model.MentorProfileModel;
import mariposas.model.MentorsEntity;
import mariposas.model.ResponseModel;
import mariposas.model.UserEntity;
import mariposas.model.UserModel;
import mariposas.repository.MenteesRepository;
import mariposas.repository.MentorsRepository;
import mariposas.repository.UserRepository;
import mariposas.service.AwsCognitoService;
import mariposas.service.UserService;

import java.math.BigDecimal;

import static mariposas.constant.AppConstant.LOGIN_SUCCESS;
import static mariposas.constant.AppConstant.PROFILE_SUCESS;
import static mariposas.constant.AppConstant.USER_ALREADY_EXISTS;
import static mariposas.constant.AppConstant.USER_CREATED;
import static mariposas.constant.AppConstant.USER_NOT_FOUND;

@Singleton
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MentorsRepository mentorsRepository;
    private final MenteesRepository menteesRepository;
    private final AwsCognitoService awsCognitoService;

    public UserServiceImpl(UserRepository userRepository,
                           MentorsRepository mentorsRepository,
                           MenteesRepository menteesRepository,
                           AwsCognitoService awsCognitoService) {

        this.userRepository = userRepository;
        this.mentorsRepository = mentorsRepository;
        this.menteesRepository = menteesRepository;
        this.awsCognitoService = awsCognitoService;
    }

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

            return buildResponse(USER_CREATED);

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

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

            return buildLoginResponse(LOGIN_SUCCESS, token.getUsername());

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Override
    public ResponseModel menteeProfile(MenteeProfileModel menteeProfileModel) {
        try {
            var existingUser = userRepository.findByEmail(menteeProfileModel.getEmail());

            if (existingUser != null) {
                var user = menteesRepository.findByUserId(existingUser);
                if (user != null) {
                    var data = MenteesEntity.builder()
                            .id(user.getId())
                            .userId(existingUser)
                            .menteeLevelId(menteeProfileModel.getMenteeLevel().getValue())
                            .isSponsored(menteeProfileModel.getIsSponsored())
                            .build();

                    menteesRepository.update(data);

                    var profile = UserEntity.builder()
                            .id(existingUser.getId())
                            .profile(menteeProfileModel.getProfile())
                            .age(menteeProfileModel.getAge())
                            .name(existingUser.getName())
                            .email(existingUser.getEmail())
                            .phone(existingUser.getPhone())
                            .isMentor(existingUser.getIsMentor())
                            .build();

                    userRepository.update(profile);
                }

                return buildResponse(PROFILE_SUCESS);

            } else {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USER_NOT_FOUND);
            }

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Override
    public ResponseModel mentorProfile(MentorProfileModel mentorProfileModel) {
        try {
            var existingUser = userRepository.findByEmail(mentorProfileModel.getEmail());

            if (existingUser != null) {
                var user = mentorsRepository.findByUserId(existingUser);
                if (user != null) {
                    var data = MentorsEntity.builder()
                            .id(user.getId())
                            .userId(existingUser)
                            .education(mentorProfileModel.getEducation())
                            .mentoringCapacity(mentorProfileModel.getMentoringCapacity().getValue())
                            .build();

                    mentorsRepository.update(data);

                    var profile = UserEntity.builder()
                            .id(existingUser.getId())
                            .profile(mentorProfileModel.getProfile())
                            .age(mentorProfileModel.getAge())
                            .name(existingUser.getName())
                            .email(existingUser.getEmail())
                            .phone(existingUser.getPhone())
                            .isMentor(existingUser.getIsMentor())
                            .build();

                    userRepository.update(profile);
                }

                return buildResponse(PROFILE_SUCESS);

            } else {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USER_NOT_FOUND);
            }

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

    private LoginResponseModel buildLoginResponse(String message, String token) {
        var loginResponse = new LoginResponseModel();
        loginResponse.setMensagem(message);
        loginResponse.setStatus(BigDecimal.valueOf(HttpStatus.CREATED.getCode()));
        loginResponse.setToken(token);
        return loginResponse;
    }
}