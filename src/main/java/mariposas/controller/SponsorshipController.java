package mariposas.controller;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import mariposas.exception.BaseException;
import mariposas.model.MentorModel;
import mariposas.model.PaginatedMentees;
import mariposas.model.ResponseModel;
import mariposas.model.SponsorshipModel;
import mariposas.model.SponsorshipNotificationModel;
import mariposas.service.JwtService;
import mariposas.service.SponsorshipService;

import static mariposas.constant.AppConstant.LOGIN_FAIL;

@Controller
public class SponsorshipController implements SponsorshipApi {
    private final JwtService jwtService;
    private final SponsorshipService sponsorshipService;

    public SponsorshipController(JwtService jwtService, SponsorshipService sponsorshipService) {
        this.jwtService = jwtService;
        this.sponsorshipService = sponsorshipService;
    }

    @Override
    public ResponseModel cancelSponsorship(String token, String emailMentee, String emailMentor) {
        if (jwtService.validate(token, emailMentor) && jwtService.isValid(token)) {
            return sponsorshipService.cancelSponsorship(emailMentee, emailMentor);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public PaginatedMentees getMenteesList(String token, String email, Integer limit, Integer page) {
        if (jwtService.validate(token, email) && jwtService.isValid(token)) {
            return sponsorshipService.getMenteesList(limit, page);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public PaginatedMentees getMentorMenteesList(String token, String email, Integer limit, Integer page) {
        if (jwtService.validate(token, email) && jwtService.isValid(token)) {
            return sponsorshipService.getMentorMenteesList(email, limit, page);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public MentorModel getMentorProfile(String token, String email) {
        if (jwtService.validate(token, email) && jwtService.isValid(token)) {
            return sponsorshipService.getMentorProfile(email);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public ResponseModel sponsoringMentee(String token, SponsorshipModel sponsorshipModel) {
        if (jwtService.validate(token, sponsorshipModel.getEmailMentor()) && jwtService.isValid(token)) {
            return sponsorshipService.sponsoringMentee(sponsorshipModel);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public SponsorshipNotificationModel sponsorshipNotification(String token, String email) {
        if (jwtService.validate(token, email) && jwtService.isValid(token)) {
            return sponsorshipService.sponsorshipNotification(email);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }
}