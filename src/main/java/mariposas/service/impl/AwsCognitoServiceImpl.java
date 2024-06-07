package mariposas.service.impl;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpStatus;
import jakarta.inject.Singleton;
import mariposas.exception.BaseException;
import mariposas.service.AwsCognitoService;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.MessageActionType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        AttributeType emailAttribute = AttributeType.builder()
                .name("email")
                .value(email)
                .build();

        AdminCreateUserRequest createUserRequest = AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(email)
                .temporaryPassword(password)
                .userAttributes(Collections.singletonList(emailAttribute))
                .messageAction(MessageActionType.SUPPRESS)
                .build();

        AdminCreateUserResponse createUserResponse = cognitoClient.adminCreateUser(createUserRequest);
        cognitoClient.adminSetUserPassword(builder -> builder
                .userPoolId(userPoolId)
                .username(email)
                .password(password)
                .permanent(true));

        return createUserResponse;
    }

    @Override
    public AdminInitiateAuthResponse authenticateUser(String email, String password) {
        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", getUsernameByEmail(email));
        authParams.put("PASSWORD", password);

        AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                .userPoolId(userPoolId)
                .clientId(clientId)
                .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .authParameters(authParams)
                .build();

        return cognitoClient.adminInitiateAuth(authRequest);
    }

    private String getUsernameByEmail(String email) {
        ListUsersResponse response = cognitoClient.listUsers(ListUsersRequest.builder()
                .userPoolId(userPoolId)
                .filter("email = \"" + email + "\"")
                .build());

        if (response.users().isEmpty()) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, USER_NOT_FOUND);
        }

        return response.users().get(0).username();
    }
}