package mariposas.controller;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.multipart.CompletedFileUpload;
import mariposas.exception.BaseException;
import mariposas.model.LoginModel;
import mariposas.model.LoginResponseModel;
import mariposas.model.MenteeProfileModel;
import mariposas.model.MentorProfileModel;
import mariposas.model.PasswordModel;
import mariposas.model.ResponseModel;
import mariposas.model.UserModel;
import mariposas.model.UserProfileModel;
import mariposas.service.JwtService;
import mariposas.service.UserService;

import static mariposas.constant.AppConstant.LOGIN_FAIL;

@Controller
public class UserController implements UserApi {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public ResponseModel changePassword(PasswordModel passwordModel) {
        return userService.changePassword(passwordModel);
    }

    @Override
    public ResponseModel createUser(UserModel user) {
        return userService.createUser(user);
    }

    @Override
    public ResponseModel deleteUser(String token, String email) {
        if (jwtService.validate(token, email) && jwtService.isValid(token)) {
            return userService.deleteUser(email);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public ResponseModel imageProfile(String token, String email, CompletedFileUpload arquivo) {
        if (jwtService.validate(token, email) && jwtService.isValid(token)) {
            return userService.imageProfile(email, arquivo);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public LoginResponseModel login(LoginModel loginModel) {
        return userService.login(loginModel);
    }

    @Override
    public ResponseModel logout(String token) {
        return userService.logout(token);
    }

    @Override
    public ResponseModel menteeProfile(String token, MenteeProfileModel menteeProfileModel) {
        if (jwtService.validate(token, menteeProfileModel.getEmail()) && jwtService.isValid(token)) {
            return userService.menteeProfile(menteeProfileModel);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public ResponseModel mentorProfile(String token, MentorProfileModel mentorProfileModel) {
        if (jwtService.validate(token, mentorProfileModel.getEmail()) && jwtService.isValid(token)) {
            return userService.mentorProfile(mentorProfileModel);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public UserProfileModel userProfile(String token, String email) {
        if (jwtService.validate(token, email) && jwtService.isValid(token)) {
            return userService.userProfile(email);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }
}