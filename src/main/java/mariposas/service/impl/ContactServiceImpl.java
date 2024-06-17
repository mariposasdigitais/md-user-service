package mariposas.service.impl;

import io.micronaut.http.HttpStatus;
import jakarta.inject.Singleton;
import mariposas.exception.BaseException;
import mariposas.model.ContactModel;
import mariposas.model.HelpModel;
import mariposas.model.ResponseModel;
import mariposas.service.ContactService;
import mariposas.service.EmailService;

import java.math.BigDecimal;

import static mariposas.constant.AppConstant.SUCCESS_SEND_MESSAGE;

@Singleton
public class ContactServiceImpl implements ContactService {
    private final EmailService emailService;

    public ContactServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public ResponseModel contactUs(ContactModel contactModel) {
        try {
            emailService.contactUs(contactModel);
            return buildResponse();
        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Override
    public ResponseModel help(HelpModel helpModel) {
        try {
            emailService.helpEmail(helpModel);
            return buildResponse();
        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    private ResponseModel buildResponse() {
        var response = new ResponseModel();
        response.setMensagem(SUCCESS_SEND_MESSAGE);
        response.setStatus(BigDecimal.valueOf(HttpStatus.CREATED.getCode()));
        return response;
    }
}