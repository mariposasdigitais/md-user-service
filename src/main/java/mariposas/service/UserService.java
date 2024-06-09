package mariposas.service;

import io.micronaut.http.multipart.CompletedFileUpload;
import mariposas.model.LoginModel;
import mariposas.model.LoginResponseModel;
import mariposas.model.MenteeProfileModel;
import mariposas.model.MentorProfileModel;
import mariposas.model.PasswordModel;
import mariposas.model.ResponseModel;
import mariposas.model.UserModel;

public interface UserService {

    ResponseModel createUser(UserModel userRequest);

    LoginResponseModel login(LoginModel loginRequest);

    ResponseModel menteeProfile(MenteeProfileModel menteeProfileModel);

    ResponseModel mentorProfile(MentorProfileModel mentorProfileModel);

    ResponseModel changePassword(PasswordModel passwordModel);

    ResponseModel deleteUser(String email);

    ResponseModel logout(String token);

    ResponseModel imageProfile(String email, CompletedFileUpload arquivo);
}