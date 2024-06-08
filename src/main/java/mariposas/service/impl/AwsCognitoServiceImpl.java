package mariposas.service.impl;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpStatus;
import jakarta.inject.Singleton;
import mariposas.exception.BaseException;
import mariposas.service.AwsCognitoService;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.MessageActionType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static mariposas.constant.AppConstant.EMAIL;
import static mariposas.constant.AppConstant.PASSWORD;
import static mariposas.constant.AppConstant.USERNAME;
import static mariposas.constant.AppConstant.USER_NOT_FOUND;

@Singleton
public class AwsCognitoServiceImpl implements AwsCognitoService {
    private final CognitoIdentityProviderClient cognitoClient;
    private final String userPoolId;
    private final String clientId;

    public AwsCognitoServiceImpl(CognitoIdentityProviderClient cognitoClient,
                                 @Value("${aws.cognito.userPoolId}") String userPoolId,
                                 @Value("${aws.cognito.clientId}") String clientId) {
        this.cognitoClient = cognitoClient;
        this.userPoolId = userPoolId;
        this.clientId = clientId;
    }

    @Override
    public AdminCreateUserResponse registerUser(String email, String password) {
        var emailAttribute = AttributeType.builder()
                .name(EMAIL)
                .value(email)
                .build();

        var createUserRequest = AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(email)
                .temporaryPassword(password)
                .userAttributes(Collections.singletonList(emailAttribute))
                .messageAction(MessageActionType.SUPPRESS)
                .build();

        var createUserResponse = cognitoClient.adminCreateUser(createUserRequest);

        cognitoClient.adminSetUserPassword(builder -> builder
                .userPoolId(userPoolId)
                .username(email)
                .password(password)
                .permanent(true));

        AttributeType emailVerifiedAttribute = AttributeType.builder()
                .name("email_verified")
                .value("true")
                .build();

        var updateUserAttributesRequest = AdminUpdateUserAttributesRequest.builder()
                .userPoolId(userPoolId)
                .username(email)
                .userAttributes(emailVerifiedAttribute)
                .build();

        cognitoClient.adminUpdateUserAttributes(updateUserAttributesRequest);

        return createUserResponse;
    }

    @Override
    public AdminInitiateAuthResponse authenticateUser(String email, String password) {
        Map<String, String> authParams = new HashMap<>();
        authParams.put(USERNAME, getUsernameByEmail(email));
        authParams.put(PASSWORD, password);

        var authRequest = AdminInitiateAuthRequest.builder()
                .userPoolId(userPoolId)
                .clientId(clientId)
                .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .authParameters(authParams)
                .build();

        return cognitoClient.adminInitiateAuth(authRequest);
    }

    @Override
    public ForgotPasswordResponse forgotPassword(String email) {
        var forgotPasswordRequest = ForgotPasswordRequest.builder()
                .clientId(clientId)
                .username(email)
                .build();

        return cognitoClient.forgotPassword(forgotPasswordRequest);
    }

    private String getUsernameByEmail(String email) {
        var response = cognitoClient.listUsers(ListUsersRequest.builder()
                .userPoolId(userPoolId)
                .filter(EMAIL.concat(" = \"" + email + "\""))
                .build());

        if (response.users().isEmpty()) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USER_NOT_FOUND);
        }

        return response.users().get(0).username();
    }
}