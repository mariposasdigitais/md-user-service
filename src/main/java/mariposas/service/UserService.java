package mariposas.service;

import io.micronaut.http.multipart.CompletedFileUpload;
import mariposas.model.LoginModel;
import mariposas.model.LoginResponseModel;
import mariposas.model.MenteeProfileModel;
import mariposas.model.MentorProfileModel;
import mariposas.model.PasswordModel;
import mariposas.model.ResponseModel;
import mariposas.model.UserModel;
import mariposas.model.UserProfileModel;

public interface UserService {
    Boolean isMentor(String email);

    ResponseModel createUser(UserModel userRequest);

    LoginResponseModel login(LoginModel loginRequest);

    ResponseModel menteeProfile(String email, MenteeProfileModel menteeProfileModel);

    ResponseModel mentorProfile(String email, MentorProfileModel mentorProfileModel);

    ResponseModel changePassword(PasswordModel passwordModel);

    ResponseModel deleteUser(String email);

    ResponseModel logout(String token);

    ResponseModel imageProfile(String email, CompletedFileUpload arquivo);

    UserProfileModel userProfile(String email);

    ResponseModel forgotPassword(String email);

    ResponseModel fullProfile(String email);
}