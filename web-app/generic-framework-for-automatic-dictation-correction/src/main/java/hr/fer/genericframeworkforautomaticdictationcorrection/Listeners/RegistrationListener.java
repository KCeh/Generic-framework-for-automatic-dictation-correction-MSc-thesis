package hr.fer.genericframeworkforautomaticdictationcorrection.Listeners;

import java.util.UUID;

import hr.fer.genericframeworkforautomaticdictationcorrection.Events.OnRegistrationCompleteEvent;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.MailService;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;


@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent>
{
    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event)
    {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event)
    {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String confirmationUrl = event.getAppUrl() + "/registrationConfirm.html?token=" + token;

        mailService.confirmRegistrationMail(recipientAddress, confirmationUrl, event.getAppUrl(), event.getLocale());
    }
}
