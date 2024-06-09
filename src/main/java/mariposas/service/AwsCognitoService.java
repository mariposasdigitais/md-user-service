package mariposas.service;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordResponse;

public interface AwsCognitoService {
    AdminCreateUserResponse registerUser(String email, String password);

    AdminInitiateAuthResponse authenticateUser(String email, String password);

    AdminDeleteUserResponse deleteUser(String email);

    AdminSetUserPasswordResponse changePassword(String email, String password);
}