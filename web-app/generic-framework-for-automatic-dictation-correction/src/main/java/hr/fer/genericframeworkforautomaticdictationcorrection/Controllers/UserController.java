package hr.fer.genericframeworkforautomaticdictationcorrection.Controllers;

import hr.fer.genericframeworkforautomaticdictationcorrection.Events.OnRegistrationCompleteEvent;
import hr.fer.genericframeworkforautomaticdictationcorrection.Exceptions.EmailExistsException;
import hr.fer.genericframeworkforautomaticdictationcorrection.Exceptions.UserNotFoundException;
import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.NewPasswordForm;
import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.NewUserForm;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.VerificationToken;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.MailService;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.UserService;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.GenericResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    MailService mailService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @RequestMapping(value = Constants.Paths.LOGIN, method = RequestMethod.GET)
    public String login() {
        return Constants.Views.LOGIN;
    }

    @RequestMapping(value = Constants.Paths.REGISTRATION, method = RequestMethod.GET)
    public String showRegistration(WebRequest request, Model model) {
        NewUserForm newUser = new NewUserForm();
        model.addAttribute("user", newUser);
        return Constants.Views.REGISTRATION;
    }

    @RequestMapping(value = Constants.Paths.REGISTRATION, method = RequestMethod.POST)
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid NewUserForm accountDto, BindingResult result, WebRequest request, Errors errors) {
        if (result.hasErrors()) {
            return new ModelAndView(Constants.Views.REGISTRATION, "user", accountDto);
        }

        User registered = createUserAccount(accountDto, result);

        if (ObjectUtils.isEmpty(registered)) {
            result.rejectValue("email", "email.exists", "User with given email already exists");
            return new ModelAndView(Constants.Views.REGISTRATION, "user", accountDto);
        }
        try {
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, appUrl, request.getLocale()));
        } catch (Exception ex) {
            return new ModelAndView(Constants.Views.EMAIL_ERROR, "user", accountDto);
        }

        return new ModelAndView(Constants.Views.REGISTRATION_SUCCESS, "user", accountDto);
    }

    private User createUserAccount(NewUserForm accountDto, BindingResult result) {
        User registered = null;
        try {
            registered = userService.registerNewUserAccount(accountDto);
        } catch (EmailExistsException e) {
            return null;
        }
        return registered;
    }

    @RequestMapping(value = Constants.Paths.REGISTRATION_COFIRMATION, method = RequestMethod.GET)
    public String confirmRegistration(WebRequest request, Model model, @RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (ObjectUtils.isEmpty(verificationToken)) {
            model.addAttribute("message", "Invalid confirmation token");
            return Constants.Redirect.BAD_USER;
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("message", "Confirmation token expired");
            return Constants.Redirect.BAD_USER;
        }

        user.setActive(true);
        userService.saveUser(user);
        return Constants.Redirect.LOGIN_CONFIRMED_ACC;
    }

    @RequestMapping(value = Constants.Paths.BAD_USER, method = RequestMethod.GET)
    public String badUser() {
        return Constants.Views.BAD_USER;
    }

    @RequestMapping(value = Constants.Paths.FORGOT_PASSWORD, method = RequestMethod.GET)
    public String forgotPassword() {
        return Constants.Views.FORGOT_PASSWORD;
    }

    @RequestMapping(value = Constants.Paths.RESET_PASSWORD, method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail) throws UserNotFoundException {
        User user = userService.findByEmail(userEmail);
        if (ObjectUtils.isEmpty(user)) {
            throw new UserNotFoundException("No user for given mail");
        }

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        String url = getAppUrl(request) + "/user/changePassword?id=" + user.getId() + "&token=" + token;
        mailService.resetPasswordMail(user.getEmail(), url, request.getLocale());
        return new GenericResponse("Reset password email sent");
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @RequestMapping(value = Constants.Paths.CHANGE_PASSWORD, method = RequestMethod.GET)
    public String showChangePasswordPage(@RequestParam("id") int id, @RequestParam("token") String token) {
        String result = userService.validatePasswordResetToken(id, token);
        if (!StringUtils.isEmpty(result)) {
            return Constants.Redirect.LOGIN_TOKEN_ERROR;
        }
        return Constants.Redirect.UPDATE_PASSWORD;
    }

    @RequestMapping(value = Constants.Paths.USER_UPDATE_PASSWORD, method = RequestMethod.GET)
    public String updatePassword() {
        return Constants.Views.UPDATE_PASSWORD;
    }

    @RequestMapping(value = Constants.Paths.SAVE_PASSWORD, method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse savePassword(@Valid NewPasswordForm passwordDto) {
        User user;
        UserDetails userDetails;
        try {
            user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user = userService.findByEmail(user.getEmail());
        } catch (ClassCastException ex) {
            userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user = userService.findByEmail(userDetails.getUsername());
        }

        userService.changeUserPassword(user, passwordDto.getNewPassword());
        SecurityContextHolder.clearContext();
        return new GenericResponse("New password saved");
    }

}
