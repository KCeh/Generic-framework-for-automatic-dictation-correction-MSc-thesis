package hr.fer.genericframeworkforautomaticdictationcorrection.Controllers;

import hr.fer.genericframeworkforautomaticdictationcorrection.Events.OnRegistrationCompleteEvent;
import hr.fer.genericframeworkforautomaticdictationcorrection.Exceptions.EmailExistsException;
import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.NewUserForm;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.VerificationToken;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.MailService;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.UserService;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Calendar;

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
    public String confirmRegistration(WebRequest request, Model model, @RequestParam("token") String token, RedirectAttributes redirectAttributes)
    {
        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (ObjectUtils.isEmpty(verificationToken))
        {
            model.addAttribute("message", "Invalid confirmation token");
            return Constants.Redirect.BAD_USER;
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0)
        {
            model.addAttribute("message", "Confirmation token expired");
            return Constants.Redirect.BAD_USER;
        }

        user.setActive(true);
        userService.saveUser(user);
        return Constants.Redirect.LOGIN_CONFIRMED_ACC;
    }

    @RequestMapping(value = Constants.Paths.BAD_USER, method = RequestMethod.GET)
    public String badUser()
    {
        return Constants.Views.BAD_USER;
    }

}
