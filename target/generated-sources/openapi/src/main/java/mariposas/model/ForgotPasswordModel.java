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
package mariposas.model;

import java.util.Objects;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.*;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import io.micronaut.core.annotation.Nullable;
import jakarta.annotation.Generated;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ForgotPasswordModel
 */
@Serdeable
@JsonPropertyOrder(ForgotPasswordModel.JSON_PROPERTY_EMAIL)
@Generated("io.micronaut.openapi.generator.JavaMicronautServerCodegen")
public class ForgotPasswordModel {

    public static final String JSON_PROPERTY_EMAIL = "email";

    @Nullable(inherited = true)
    @Schema(name = "email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty(JSON_PROPERTY_EMAIL)
    @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
    private String email;

    /**
     * @return the email property value
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email property value
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Set email in a chainable fashion.
     *
     * @return The same instance of ForgotPasswordModel for chaining.
     */
    public ForgotPasswordModel email(String email) {
        this.email = email;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ForgotPasswordModel forgotPasswordModel = (ForgotPasswordModel) o;
        return Objects.equals(email, forgotPasswordModel.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "ForgotPasswordModel("
            + "email: " + getEmail()
            + ")";
    }

}