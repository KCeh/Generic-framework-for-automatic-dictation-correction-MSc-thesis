package hr.fer.genericframeworkforautomaticdictationcorrection.Controllers;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.ManageUsersPostModel;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Role;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.UserViewModel;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.RoleService;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.UserService;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.GenericResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

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

    @RequestMapping(value=Constants.Paths.PROMOTE_USER, method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse promote(@RequestParam("email") String email){
        User user = userService.findByEmail(email);

        if(!ObjectUtils.isEmpty(user)){
            Collection<Role> roles = user.getRoles();

            if(roles.size()==3){
                return new GenericResponse("Error","User+can't+be+promoted+further");
            }else {
                if(roles.size()==1){
                    Role role = roleService.findByName("ROLE_TEACHER");
                    roles.add(role);
                }else if(roles.size()==2){
                    Role role = roleService.findByName("ROLE_ADMIN");
                    roles.add(role);
                }
                Collection<Role> sorted = roles.stream().sorted(Comparator.comparingLong(Role::getId)).collect(Collectors.toList());
                user.setRoles(sorted);
                userService.updateUser(user);
            }
        }else {
            return new GenericResponse("Error","User+can't+be+found");
        }
        return new GenericResponse("Success");
    }

    @RequestMapping(value=Constants.Paths.DEMOTE_USER, method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse demote(@RequestParam("email") String email){
        User user = userService.findByEmail(email);

        if(!ObjectUtils.isEmpty(user)){
            Collection<Role> roles = user.getRoles();

            if(roles.size()==1){
                return new GenericResponse("Error","User+can't+be+demoted+further");
            }else {
                if(roles.size()==2){
                    Role role = roleService.findByName("ROLE_TEACHER");
                    roles.remove(role);
                }else if(roles.size()==3){
                    Role role = roleService.findByName("ROLE_ADMIN");
                    roles.remove(role);
                }
                Collection<Role> sorted = roles.stream().sorted(Comparator.comparingLong(Role::getId)).collect(Collectors.toList());
                user.setRoles(sorted);
                userService.updateUser(user);
            }
        }else {
            return new GenericResponse("Error","User+can't+be+found");
        }
        return new GenericResponse("Success");
    }

}
