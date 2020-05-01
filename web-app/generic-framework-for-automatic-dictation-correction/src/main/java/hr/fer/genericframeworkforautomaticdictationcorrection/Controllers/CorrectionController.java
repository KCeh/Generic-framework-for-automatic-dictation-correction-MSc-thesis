package hr.fer.genericframeworkforautomaticdictationcorrection.Controllers;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.CorrectedDictation;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.*;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping(Constants.Paths.CORRECTION_MY)
    public String showMyCorrections(Model model){
        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findByEmail(userDetails.getUsername());
        List<CorrectedDictation> correctedDictationList = correctedDictationService.findByUser(currentUser);

        model.addAttribute("corrections",correctedDictationList);

        return Constants.Views.VIEW_CORRECTIONS;
    }
}
