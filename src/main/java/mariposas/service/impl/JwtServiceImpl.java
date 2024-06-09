package mariposas.service.impl;

import com.nimbusds.jwt.JWTParser;
import io.micronaut.http.HttpStatus;
import jakarta.inject.Singleton;
import mariposas.exception.BaseException;
import mariposas.model.InvalidTokenEntity;
import mariposas.repository.InvalidTokenRepository;
import mariposas.service.JwtService;

import java.text.ParseException;
import java.time.Instant;

import static mariposas.constant.AppConstant.LOGIN_FAIL;

@Singleton
public class JwtServiceImpl implements JwtService {
    private final InvalidTokenRepository invalidTokenRepository;

    public JwtServiceImpl(InvalidTokenRepository invalidTokenRepository) {

        this.invalidTokenRepository = invalidTokenRepository;
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

    @Override
    public Void invalidateToken(String token) {
        var invalidToken = InvalidTokenEntity
                .builder()
                .token(token)
                .invalidatedAt(Instant.now())
                .build();

        invalidTokenRepository.save(invalidToken);
        return null;
    }

    @Override
    public Boolean isValid(String token) {
        var response = invalidTokenRepository.findByToken(token);
        return response == null;
    }
}