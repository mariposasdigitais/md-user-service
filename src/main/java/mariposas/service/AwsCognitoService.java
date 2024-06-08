package mariposas.service;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordResponse;

public interface AwsCognitoService {
    AdminCreateUserResponse registerUser(String email, String password);

    AdminInitiateAuthResponse authenticateUser(String email, String password);

    ForgotPasswordResponse forgotPassword(String email);
}