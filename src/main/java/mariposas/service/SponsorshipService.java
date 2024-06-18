package mariposas.service;

import mariposas.model.MentorModel;
import mariposas.model.PaginatedMentees;
import mariposas.model.ResponseModel;
import mariposas.model.SponsorshipModel;
import mariposas.model.SponsorshipNotificationModel;

public interface SponsorshipService {

    PaginatedMentees getMenteesList(Integer limit, Integer page);

    ResponseModel sponsoringMentee(SponsorshipModel sponsorshipModel);

    ResponseModel cancelSponsorship(String emailMentee, String emailMentor);

    MentorModel getMentorProfile(String email);

    SponsorshipNotificationModel sponsorshipNotification(String email);

    PaginatedMentees getMentorMenteesList(String email, Integer limit, Integer page);
}