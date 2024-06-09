package mariposas.service;

public interface JwtService {
    Boolean validate(String token, String email);

    Void invalidateToken(String token);

    Boolean isValid(String token);
}