package mariposas.controller;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import jakarta.validation.Valid;
import mariposas.exception.BaseException;
import mariposas.model.InvalidMenteeModelInner;
import mariposas.model.MenteesModelInner;
import mariposas.model.MentorModelInner;
import mariposas.model.ResponseModel;
import mariposas.model.SponsorshipModel;
import mariposas.model.SponsorshipNotificationModel;
import mariposas.service.JwtService;
import mariposas.service.SponsorshipService;
import mariposas.service.UserService;

import java.util.List;

import static mariposas.constant.AppConstant.LOGIN_FAIL;

@Controller
public class SponsorshipController implements SponsorshipApi {
    private final JwtService jwtService;
    private final SponsorshipService sponsorshipService;
    private final UserService userService;

    public SponsorshipController(JwtService jwtService, SponsorshipService sponsorshipService, UserService userService) {
        this.jwtService = jwtService;
        this.sponsorshipService = sponsorshipService;
        this.userService = userService;
    }

    @Override
    public ResponseModel cancelSponsorship(String token, String emailMentee, String emailMentor) {
        if (!userService.isMentor(emailMentor)) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }

        if (jwtService.validate(token, emailMentor) && jwtService.isValid(token)) {
            return sponsorshipService.cancelSponsorship(emailMentee, emailMentor);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public List<@Valid MenteesModelInner> getMenteesList(String token, String email) {
        if (!userService.isMentor(email)) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }

        if (jwtService.validate(token, email) && jwtService.isValid(token)) {
            return sponsorshipService.getMenteesList(email);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public List<@Valid MenteesModelInner> getMentorMenteesList(String token, String email) {
        if (!userService.isMentor(email)) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }

        if (jwtService.validate(token, email) && jwtService.isValid(token)) {
            return sponsorshipService.getMentorMenteesList(email);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public List<@Valid MentorModelInner> getMentorProfile(String token, String email) {
        if (jwtService.validate(token, email) && jwtService.isValid(token)) {
            return sponsorshipService.getMentorProfile(email);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public ResponseModel invalidMentee(String token, String email, List<@Valid InvalidMenteeModelInner> invalidMenteeModelInners) {
        if (jwtService.validate(token, email) && jwtService.isValid(token)) {
            return sponsorshipService.invalidMentee(email, invalidMenteeModelInners);
        } else {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }
    }

    @Override
    public ResponseModel sponsoringMentee(String token, SponsorshipModel sponsorshipModel) {
        if (!userService.isMentor(sponsorshipModel.getEmailMentor())) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, LOGIN_FAIL);
        }

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