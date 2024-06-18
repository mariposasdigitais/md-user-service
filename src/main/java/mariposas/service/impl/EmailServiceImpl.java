package mariposas.service.impl;

import io.micronaut.http.HttpStatus;
import jakarta.inject.Singleton;
import mariposas.exception.BaseException;
import mariposas.model.ContactModel;
import mariposas.model.HelpModel;
import mariposas.service.EmailService;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.VerifyEmailIdentityRequest;

import static mariposas.constant.AppConstant.EMAIL_MARIPOSAS;

@Singleton
public class EmailServiceImpl implements EmailService {
    private final SesClient sesClient;

    public EmailServiceImpl(SesClient sesClient) {
        this.sesClient = sesClient;
    }

    @Override
    public Void forgotPasswordEmail(String recipient) {
        try {
            var htmlBody = "<html>" +
                    "<body style='background-color: #ffffff; font-family: Arial, sans-serif; margin: 0;'>" +
                    "<header style='background-color: #92cae7; color: white; padding: 20px; text-align: center;'>" +
                    "<img src='https://mariposas-digitais-front-bh67dloi7a-ue.a.run.app/images//logo-txt-img.png' alt='Logo da aplicação Mariposas Digitais' style='display: block; margin: 0 auto; width: 130px; text-align: center;'>" +
                    "</header>" +
                    "<div style='background-color: #ffffff; padding: 20px; text-align: center;'>" +
                    "<main>" +
                    "<p>Olá, mariposa!<br>" +
                    "Recebemos uma solicitação para redefinição de senha em nosso site. Por favor, clique no link abaixo para concluir o processo.<br>" +
                    "Caso você não tenha solicitado a redefinição, desconsidere este e-mail.</p>" +
                    "<br>" +
                    "<div style='text-align: center; margin-top: 20px;'>" +
                    "<a href='https://mariposas-digitais-front-bh67dloi7a-ue.a.run.app/reset' style='background-color: #d457d2; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Clique aqui para redefinir senha</a>" +
                    "</div>" +
                    "</main>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            var request = SendEmailRequest.builder()
                    .source(EMAIL_MARIPOSAS)
                    .destination(Destination.builder().toAddresses(recipient).build())
                    .message(Message.builder()
                            .subject(Content.builder().data("Reset de senha | Mariposas Digitais").build())
                            .body(Body.builder().html(Content.builder().data(htmlBody).build()).build())
                            .build())
                    .build();

            sesClient.sendEmail(request);
            return null;

        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Override
    public Void confirmEmail(String recipient) {
        try {
            var request = VerifyEmailIdentityRequest.builder()
                    .emailAddress(recipient)
                    .build();

            sesClient.verifyEmailIdentity(request);
            return null;
        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Override
    public Void contactUs(ContactModel contactModel) {
        try {
            var body = "Nome: " + contactModel.getName()
                    .concat(" | Telefone: ").concat(contactModel.getPhone())
                    .concat(" | Email: ").concat(contactModel.getEmail())
                    .concat("\n").concat(contactModel.getMessage());

            var request = SendEmailRequest.builder()
                    .source(EMAIL_MARIPOSAS)
                    .destination(dest -> dest.toAddresses(EMAIL_MARIPOSAS))
                    .message(msg -> msg
                            .subject(sub -> sub.data("Fale conosco | Mariposas Digitais"))
                            .body(b -> b.text(t -> t.data(body))))
                    .build();

            sesClient.sendEmail(request);
            return null;
        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @Override
    public Void helpEmail(HelpModel helpModel) {
        try {
            var body = "Usuária: " + helpModel.getName()
                    .concat(" | Tipo: ").concat(helpModel.getUserType())
                    .concat(" | E-mail: ").concat(helpModel.getEmail())
                    .concat("\n").concat(helpModel.getMessage());

            var request = SendEmailRequest.builder()
                    .source(EMAIL_MARIPOSAS)
                    .destination(dest -> dest.toAddresses(EMAIL_MARIPOSAS))
                    .message(msg -> msg
                            .subject(sub -> sub.data("Ajuda | Mariposas Digitais"))
                            .body(b -> b.text(t -> t.data(body))))
                    .build();

            sesClient.sendEmail(request);
            return null;
        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }
}