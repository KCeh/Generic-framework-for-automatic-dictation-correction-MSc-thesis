package hr.fer.genericframeworkforautomaticdictationcorrection.Controllers;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.UserViewModel;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.UserService;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    UserService userService;

    @RequestMapping(Constants.Paths.MANAGE_USERS)
    public String manage(SecurityContextHolderAwareRequestWrapper requestWrapper, Model model, final HttpServletRequest request){
        if (!ObjectUtils.isEmpty(SecurityContextHolder.getContext().getAuthentication()) && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) && requestWrapper.isUserInRole(Constants.RoleNames.ADMIN)){


            //check
            org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User currentUser = userService.findByUserDetailsUsername(userDetails.getUsername());
            List<User> allUsers = userService.findAll();
            allUsers.remove(currentUser);

            List<UserViewModel> viewModel = allUsers.stream().map(user -> new UserViewModel(user)).collect(Collectors.toList());

            model.addAttribute("users", viewModel);

            return Constants.Paths.MANAGE_USERS;

        }else {
            return Constants.Redirect.BASE_PATH_ACCESS_DENIED;
        }
    }


}
