package hr.fer.genericframeworkforautomaticdictationcorrection.Utils;

public interface Constants {

    interface Paths{
        String BASE_PATH="/";
        String HOME="/home";

        String LOGIN ="/login";
        String LOGIN_WITH_PARMS = "/login*";
        String LOGOUT="/logout";
        String REGISTRATION="/signup";
        String REGISTRATION_COFIRMATION = "/registrationConfirm*";
        String BAD_USER="/badUser";

        String RESOURCES = "/resources/**";
        String JS = "/js/**";
        String CSS = "/css/**";
        String WEBJARS = "/webjars/**";
    }

    interface Redirect{
        String BASE_PATH="redirect:/";
        String LOGIN="redirect:/login";
        String BAD_USER = "redirect:/badUser";
        String LOGIN_CONFIRMED_ACC="redirect:/login?message=Account+confirmed";
    }

    interface Views{
        String INDEX = "index";
        String LOGIN = "loginForm";
        String REGISTRATION ="registration";
        String REGISTRATION_SUCCESS="successfulRegistration";
        String EMAIL_ERROR="emailError";
        String BAD_USER="badUser";
    }
}
