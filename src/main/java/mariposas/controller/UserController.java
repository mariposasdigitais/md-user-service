package mariposas.controller;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import mariposas.exception.BaseException;
import mariposas.model.LoginModel;
import mariposas.model.LoginResponseModel;
import mariposas.model.MenteeProfileModel;
import mariposas.model.MentorProfileModel;
import mariposas.model.ResponseModel;
import mariposas.model.UserModel;
import mariposas.service.JwtAuthenticationService;
import mariposas.service.UserService;

import static mariposas.constant.AppConstant.LOGIN_FAIL;

@Controller
public class UserController implements UserApi {

    private final UserService userService;
    private final JwtAuthenticationService jwtAuthenticationService;

    public UserController(UserService userService, JwtAuthenticationService jwtAuthenticationService) {
        this.userService = userService;
        this.jwtAuthenticationService = jwtAuthenticationService;
    }

    @Override
    public ResponseModel createUser(UserModel user) {
        return userService.createUser(user);
    }

    @Override
    public LoginResponseModel login(LoginModel loginModel) {
        return userService.login(loginModel);
    }

    @Override
    public ResponseModel menteeProfile(String token, MenteeProfileModel menteeProfileModel) {
        if (jwtAuthenticationService.validate(token, menteeProfileModel.getEmail())) {
            return userService.menteeProfile(menteeProfileModel);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public ResponseModel mentorProfile(String token, MentorProfileModel mentorProfileModel) {
        if (jwtAuthenticationService.validate(token, mentorProfileModel.getEmail())) {
            return userService.mentorProfile(mentorProfileModel);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }
}