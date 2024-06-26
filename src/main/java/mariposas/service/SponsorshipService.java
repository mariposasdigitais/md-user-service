package mariposas.service;

import mariposas.model.InvalidMenteeModelInner;
import mariposas.model.MenteesModelInner;
import mariposas.model.MentorModelInner;
import mariposas.model.ResponseModel;
import mariposas.model.SponsorshipModel;
import mariposas.model.SponsorshipNotificationModel;

import java.util.List;

public interface SponsorshipService {

    List<MenteesModelInner> getMenteesList(String email);

    ResponseModel sponsoringMentee(SponsorshipModel sponsorshipModel);

    ResponseModel cancelSponsorship(String emailMentee, String emailMentor);

    List<MentorModelInner> getMentorProfile(String email);

    SponsorshipNotificationModel sponsorshipNotification(String email);

    List<MenteesModelInner> getMentorMenteesList(String email);
    ResponseModel invalidMentee(String email, List<InvalidMenteeModelInner> invalidMenteeModelInners);
}