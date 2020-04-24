package hr.fer.genericframeworkforautomaticdictationcorrection.Services.Implementations;

import hr.fer.genericframeworkforautomaticdictationcorrection.Services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MailServiceImplementation implements MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MessageSource messageSource;

    private static final String SERVER = " http://localhost:8000"; //fix

    @Override
    public void confirmRegistrationMail(String emailAddress, String confirmationUrl, String baseUrl, Locale locale) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(emailAddress);
        email.setSubject(messageSource.getMessage("mail.registration.subject", null, locale));
        email.setText(messageSource.getMessage("mail.registration.click", null, locale) + SERVER + confirmationUrl);
        mailSender.send(email);
    }

    @Override
    public void resetPasswordMail(String emailAddress, String url) {

    }
}
