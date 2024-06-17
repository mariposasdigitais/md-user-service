package mariposas.service;

import mariposas.model.ContactModel;
import mariposas.model.HelpModel;
import mariposas.model.ResponseModel;

public interface ContactService {
    ResponseModel contactUs(ContactModel contactModel);

    ResponseModel help(HelpModel helpModel);
}