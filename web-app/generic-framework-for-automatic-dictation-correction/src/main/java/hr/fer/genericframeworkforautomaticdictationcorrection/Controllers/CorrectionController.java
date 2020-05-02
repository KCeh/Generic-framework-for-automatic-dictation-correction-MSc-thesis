package hr.fer.genericframeworkforautomaticdictationcorrection.Controllers;

import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.NewCorrectionForm;
import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.NewDictateFrom;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.*;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.*;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@Controller
public class CorrectionController {
    @Autowired
    UserService userService;

    @Autowired
    DictateService dictateService;

    @Autowired
    LanguageService languageService;

    @Autowired
    CorrectedDictationService correctedDictationService;

    @Autowired
    StorageService storageService;

    @Autowired
    RoleService roleService;

    @RequestMapping(Constants.Paths.CORRECTION_MY)
    public String showMyCorrections(Model model){
        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findByEmail(userDetails.getUsername());
        List<CorrectedDictation> correctedDictationList = correctedDictationService.findByUser(currentUser);

        model.addAttribute("corrections",correctedDictationList);

        return Constants.Views.VIEW_CORRECTIONS;
    }

    @RequestMapping(value = Constants.Paths.CORRECTION_VIEW, method = RequestMethod.GET)
    public String viewDictate(@RequestParam int id, Model model) {
        CorrectedDictation correctedDictation = correctedDictationService.findById((long)id);

        if(ObjectUtils.isEmpty(correctedDictation)){
            return Constants.Redirect.CORRECTION_ERROR;
        }

        NewCorrectionForm newCorrectionForm = new NewCorrectionForm(correctedDictation);
        model.addAttribute("correction", newCorrectionForm);
        return Constants.Views.VIEW_DICTATE;
    }

    @RequestMapping(value = Constants.Paths.CORRECTION_CREATE, method = RequestMethod.GET)
    public String createDictate(Model model) {
        NewCorrectionForm newCorrectionForm = new NewCorrectionForm();
        model.addAttribute("correction", newCorrectionForm);

        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        Role teacher = roleService.findByName("ROLE_TEACHER");
        Collection<Role> roles = user.getRoles();
        List<Dictate> dictates;
        if(roles.contains(teacher)){
            dictates=dictateService.findAll();
        }else{
            dictates=dictateService.findByUser(user);
        }

        model.addAttribute("dictates", dictates);
        return Constants.Views.CREATE_DICTATE;
    }
}
