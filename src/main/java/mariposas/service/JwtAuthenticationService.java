package mariposas.service;

public interface JwtAuthenticationService {
    Boolean validate(String token, String email);
}