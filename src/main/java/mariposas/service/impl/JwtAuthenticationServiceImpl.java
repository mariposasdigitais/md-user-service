package mariposas.service.impl;

import com.nimbusds.jwt.JWTParser;
import io.micronaut.http.HttpStatus;
import jakarta.inject.Singleton;
import mariposas.exception.BaseException;
import mariposas.service.JwtAuthenticationService;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import java.text.ParseException;

import static mariposas.constant.AppConstant.LOGIN_FAIL;

@Singleton
public class JwtAuthenticationServiceImpl implements JwtAuthenticationService {
    private final CognitoIdentityProviderClient cognitoClient;

    public JwtAuthenticationServiceImpl(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

    @Override
    public Boolean validate(String token, String email) {
        try {
            var jwt = JWTParser.parse(token);
            var claimsSet = jwt.getJWTClaimsSet();
            var userEmail = claimsSet.getClaim("email").toString();

            if (userEmail == null) {
                throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
            }

            return userEmail.equals(email);

        } catch (ParseException e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.toString());
        }
    }
}