package hr.fer.genericframeworkforautomaticdictationcorrection.Events;

import java.util.Locale;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import org.springframework.context.ApplicationEvent;

public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private User user;
    private Locale locale;

    public OnRegistrationCompleteEvent(User user, String appUrl, Locale locale) {
        super(user);
        this.user = user;
        this.appUrl = appUrl;
        this.locale = locale;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
