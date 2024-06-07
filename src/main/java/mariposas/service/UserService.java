package mariposas.service;

import mariposas.model.LoginModel;
import mariposas.model.LoginResponseModel;
import mariposas.model.MenteeProfileModel;
import mariposas.model.MentorProfileModel;
import mariposas.model.ResponseModel;
import mariposas.model.UserModel;

public interface UserService {

    ResponseModel createUser(UserModel userRequest);

    LoginResponseModel login(LoginModel loginRequest);

    ResponseModel menteeProfile(MenteeProfileModel menteeProfileModel);

    ResponseModel mentorProfile(MentorProfileModel mentorProfileModel);
}