package mariposas.controller;

import io.micronaut.http.annotation.Controller;
import mariposas.model.ContactModel;
import mariposas.model.HelpModel;
import mariposas.model.ResponseModel;
import mariposas.service.ContactService;

@Controller
public class ContactController implements ContactApi {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    public ResponseModel contactUs(ContactModel contactModel) {
        return contactService.contactUs(contactModel);
    }

    @Override
    public ResponseModel help(HelpModel helpModel) {
        return contactService.help(helpModel);
    }
}