package mariposas.service;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;

public interface AwsCognitoService {
    AdminCreateUserResponse registerUser(String email, String password);

    AdminInitiateAuthResponse authenticateUser(String email, String password);
}
