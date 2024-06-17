package mariposas.service;

import mariposas.model.ContactModel;
import mariposas.model.HelpModel;

public interface EmailService {
    Void forgotPasswordEmail(String recipient);

    Void confirmEmail(String recipient);

    Void contactUs(ContactModel contactModel);

    Void helpEmail(HelpModel helpModel);
}
