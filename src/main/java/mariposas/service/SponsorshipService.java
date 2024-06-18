package mariposas.service;

import jakarta.validation.Valid;
import mariposas.model.MenteesModelInner;
import mariposas.model.MentorModel;
import mariposas.model.ResponseModel;
import mariposas.model.SponsorshipModel;
import mariposas.model.SponsorshipNotificationModel;

import java.util.List;

public interface SponsorshipService {

    List<@Valid MenteesModelInner> getMenteesList();

    ResponseModel sponsoringMentee(SponsorshipModel sponsorshipModel);

    ResponseModel cancelSponsorship(String emailMentee, String emailMentor);

    MentorModel getMentorProfile(String email);

    SponsorshipNotificationModel sponsorshipNotification(String email);

    List<@Valid MenteesModelInner> getMentorMenteesList(String email);
}