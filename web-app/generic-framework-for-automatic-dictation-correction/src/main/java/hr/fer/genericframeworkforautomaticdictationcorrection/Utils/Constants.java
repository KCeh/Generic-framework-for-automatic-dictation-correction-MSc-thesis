package hr.fer.genericframeworkforautomaticdictationcorrection.Utils;

public interface Constants {

    interface Paths{
        String BASE_PATH="/";
        String HOME="/home";

        String LOGIN ="/login";
        String LOGIN_WITH_PARMS = "/login*";
        String LOGOUT="/logout";

        String RESOURCES = "/resources/**";
        String JS = "/js/**";
        String CSS = "/css/**";
        String WEBJARS = "/webjars/**";
    }

    interface Redirect{
        String BASE_PATH="redirect:/";
    }

    interface Views{
        String INDEX = "index";
        String LOGIN = "loginForm";
    }
}
