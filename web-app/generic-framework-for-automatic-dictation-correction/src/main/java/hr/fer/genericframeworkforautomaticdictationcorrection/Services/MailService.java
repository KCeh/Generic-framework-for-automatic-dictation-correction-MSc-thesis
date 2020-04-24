package hr.fer.genericframeworkforautomaticdictationcorrection.Services;

import java.util.Locale;

public interface MailService {
    void confirmRegistrationMail(String emailAddress, String confirmationUrl, String baseUrl, Locale locale);

    void resetPasswordMail(String emailAddress, String url);
}
