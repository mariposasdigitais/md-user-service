/*
 * md-user-service
 * Microsserviço responsável por gerir todas as ações relacionadas a um usuário.
 *
 * The version of the OpenAPI document: 1.0.11
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package mariposas.controller;

import io.micronaut.http.annotation.*;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.format.Format;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import mariposas.model.ForgotPasswordModel;
import mariposas.model.LoginModel;
import mariposas.model.LoginResponseModel;
import mariposas.model.MenteeProfileModel;
import mariposas.model.MentorProfileModel;
import mariposas.model.PaginatedMentees;
import mariposas.model.ResponseModel;
import mariposas.model.UserModel;
import jakarta.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Generated("io.micronaut.openapi.generator.JavaMicronautServerCodegen")
@Controller
@Tag(name = "User", description = "The User API")
public interface UserApi {

    /**
     * {@summary Create user}
     *
     * @param userModel (optional)
     * @return ResponseModel
     */
    @Operation(
        operationId = "createUser",
        summary = "Create user",
        responses = {
            @ApiResponse(responseCode = "200", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseModel.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
        }
    )
    @Post("/user")
    @Secured(SecurityRule.IS_ANONYMOUS)
    ResponseModel createUser(
        @Body @Nullable(inherited = true) @Valid UserModel userModel
    );

    /**
     * {@summary Request password reset link}
     *
     * @param token JWT User Access Token (required)
     * @param forgotPasswordModel (optional)
     * @return ResponseModel
     */
    @Operation(
        operationId = "forgotPassword",
        summary = "Request password reset link",
        responses = {
            @ApiResponse(responseCode = "200", description = "Password recovery request sent successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseModel.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
        }
    )
    @Post("/user/password/forgot")
    @Secured(SecurityRule.IS_ANONYMOUS)
    ResponseModel forgotPassword(
        @Header("token") @NotNull String token,
        @Body @Nullable(inherited = true) @Valid ForgotPasswordModel forgotPasswordModel
    );

    /**
     * {@summary Obtain a list of mentees available for sponsorship}
     *
     * @param token JWT User Access Token (required)
     * @param email User email (required)
     * @param limit Number of records per page (optional, default to 2)
     * @param page Current page (optional, default to 1)
     * @return PaginatedMentees
     */
    @Operation(
        operationId = "getMenteesList",
        summary = "Obtain a list of mentees available for sponsorship",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of mentees successfully returned", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedMentees.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
        }
    )
    @Get("/user/mentee")
    @Secured(SecurityRule.IS_ANONYMOUS)
    PaginatedMentees getMenteesList(
        @Header("token") @NotNull String token,
        @Header("email") @NotNull String email,
        @QueryValue(value = "limit", defaultValue = "2") @Nullable(inherited = true) @Min(2) Integer limit,
        @QueryValue(value = "page", defaultValue = "1") @Nullable(inherited = true) @Min(1) Integer page
    );

    /**
     * {@summary Login}
     *
     * @param loginModel (optional)
     * @return LoginResponseModel
     */
    @Operation(
        operationId = "login",
        summary = "Login",
        responses = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseModel.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
        }
    )
    @Post("/user/login")
    @Secured(SecurityRule.IS_ANONYMOUS)
    LoginResponseModel login(
        @Body @Nullable(inherited = true) @Valid LoginModel loginModel
    );

    /**
     * {@summary Complete mentee profile}
     *
     * @param token JWT User Access Token (required)
     * @param menteeProfileModel (optional)
     * @return ResponseModel
     */
    @Operation(
        operationId = "menteeProfile",
        summary = "Complete mentee profile",
        responses = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseModel.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
        }
    )
    @Post("/user/profile/mentee")
    @Secured(SecurityRule.IS_ANONYMOUS)
    ResponseModel menteeProfile(
        @Header("token") @NotNull String token,
        @Body @Nullable(inherited = true) @Valid MenteeProfileModel menteeProfileModel
    );

    /**
     * {@summary Complete mentor profile}
     *
     * @param token JWT User Access Token (required)
     * @param mentorProfileModel (optional)
     * @return ResponseModel
     */
    @Operation(
        operationId = "mentorProfile",
        summary = "Complete mentor profile",
        responses = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseModel.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
        }
    )
    @Post("/user/profile/mentor")
    @Secured(SecurityRule.IS_ANONYMOUS)
    ResponseModel mentorProfile(
        @Header("token") @NotNull String token,
        @Body @Nullable(inherited = true) @Valid MentorProfileModel mentorProfileModel
    );

}