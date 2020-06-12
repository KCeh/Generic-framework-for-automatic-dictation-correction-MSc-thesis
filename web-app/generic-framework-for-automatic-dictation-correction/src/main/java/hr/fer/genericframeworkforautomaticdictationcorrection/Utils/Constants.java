package hr.fer.genericframeworkforautomaticdictationcorrection.Utils;

public interface Constants {

    interface Paths{
        String BASE_PATH="/";
        String HOME="/home";

        String ERROR="/error";

        String LOGIN ="/login";
        String LOGIN_WITH_PARMS = "/login*";
        String LOGOUT="/logout";
        String REGISTRATION="/signup";
        String REGISTRATION_COFIRMATION = "/registrationConfirm*";
        String BAD_USER="/badUser";
        String FORGOT_PASSWORD = "/forgotPassword";
        String RESET_PASSWORD  = "/user/resetPassword*";
        String CHANGE_PASSWORD = "/user/changePassword*";
        String SAVE_PASSWORD = "/user/savePassword*";
        String USER_UPDATE_PASSWORD = "/user/updatePassword*";

        String MANAGE_USERS="/manageUsers";
        String PROMOTE_USER="/manageUsers/promote";
        String DEMOTE_USER="/manageUsers/demote";

        String DICTATE_MY="/dictate/my";
        String DICTATE_ALL="/dictate/all";
        String DICTATE_VIEW="/dictate/view";
        String DICTATE_CREATE="/dictate/create";
        String DICTATE_EDIT="/dictate/edit";
        String DICTATE_DELETE="/dictate/delete";
        String DICTATE_UPLOAD_AUDIO="/dictate/uploadAudio";
        String DICTATE_TRANSCRIBE="/dictate/transcribe";

        String CORRECTION_MY="/corrections/my";
        String CORRECTION_VIEW="/corrections/view";
        String CORRECTION_CREATE="/corrections/create";
        String CORRECTION_CREATE_MULTIPLE="/corrections/createMultiple";;
        String CORRECTION_UPLOAD_IMAGE="/corrections/uploadImage";
        String CORRECTION_UPLOAD_MULTIPLE="/corrections/uploadMultiple";
        String CORRECTION_DELETE="/corrections/delete";
        String CORRECTION_EDIT="/corrections/edit";

        String RESOURCES = "/resources/**";
        String JS = "/js/**";
        String CSS = "/css/**";
        String WEBJARS = "/webjars/**";
    }

    interface Redirect{
        String BASE_PATH="redirect:/";
        String BASE_PATH_ACCESS_DENIED="redirect:/?message=Access+denied";
        String LOGIN="redirect:/login";
        String BAD_USER = "redirect:/badUser";
        String LOGIN_CONFIRMED_ACC="redirect:/login?message=Account+confirmed";
        String LOGIN_TOKEN_ERROR="redirect:/login?message=Token+error";
        String UPDATE_PASSWORD = "redirect:/user/updatePassword";
        String DICTATE_MY="redirect:/dictate/my";
        String DICTATE_ERROR="redirect:/dictate/my?error=Something+went+wrong";
        String DICTATE_NO_AUTHORITY="redirect:/dictate/my?error=You+don't+have+authority";
        String CORRECTION_ERROR="redirect:/corrections/my?error=Something+went+wrong";
        String CORRECTION_MY="redirect:/corrections/my";
    }

    interface Views{
        String INDEX = "index";
        String LOGIN = "loginForm";
        String REGISTRATION ="registration";
        String REGISTRATION_SUCCESS="successfulRegistration";
        String EMAIL_ERROR="emailError";
        String BAD_USER="badUser";
        String FORGOT_PASSWORD="forgotPassword";
        String UPDATE_PASSWORD="updatePassword";
        String ERROR="error";
        String MANAGE_USERS="manageUsers";
        String DICTATES="dictates";
        String VIEW_DICTATE="viewDictate";
        String CREATE_DICTATE="createDictate";
        String EDIT_DICTATE="editDictate";
        String VIEW_CORRECTIONS="corrections";
        String CREATE_CORRECTION="createCorrection";
        String CREATE_MULTIPLE_CORRECTIONS="createMultipleCorrections";
        String VIEW_CORRECTION="viewCorrection";
        String EDIT_CORRECTION="editCorrection";
    }

    interface Authority{
        String CHANGE_PASSWORD="CHANGE_PASSWORD_PRIVILEGE";
    }

    interface RoleNames{
        String ADMIN="ROLE_ADMIN";
        String TEACHER="ROLE_TEACHER";
        String USER="ROLE_USER";
    }
}
