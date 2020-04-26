package hr.fer.genericframeworkforautomaticdictationcorrection.Controllers;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Dictate;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.DictateService;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.UserService;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class DictateController {

    @Autowired
    UserService userService;

    @Autowired
    DictateService dictateService;

    @RequestMapping(Constants.Paths.DICTATE_ALL)
    public String showAllDictates(Model model){
        List<Dictate> allDictates = dictateService.findAll();
        model.addAttribute("dictates",allDictates);

        return Constants.Views.DICTATES;
    }

    @RequestMapping(Constants.Paths.DICTATE_MY)
    public String showMyDictates(Model model){
        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findByEmail(userDetails.getUsername());
        List<Dictate> myDictates = dictateService.findByUser(currentUser);
        model.addAttribute("dictates",myDictates);

        return Constants.Views.DICTATES;
    }
}
