package hr.fer.genericframeworkforautomaticdictationcorrection.Controllers;

import hr.fer.genericframeworkforautomaticdictationcorrection.Exceptions.UserNotFoundException;
import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.NewDictateFrom;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.CorrectedDictation;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Dictate;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Language;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.*;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.GenericResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
public class DictateController {

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

    @RequestMapping(Constants.Paths.DICTATE_ALL)
    public String showAllDictates(Model model) {
        List<Dictate> allDictates = dictateService.findAll();
        model.addAttribute("dictates", allDictates);

        return Constants.Views.DICTATES;
    }

    @RequestMapping(Constants.Paths.DICTATE_MY)
    public String showMyDictates(Model model) {
        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findByEmail(userDetails.getUsername());
        List<Dictate> myDictates = dictateService.findByUser(currentUser);
        model.addAttribute("dictates", myDictates);

        return Constants.Views.DICTATES;
    }

    @RequestMapping(value = Constants.Paths.DICTATE_VIEW, method = RequestMethod.GET)
    public String viewDictate(@RequestParam int id, Model model) {
        Dictate dictate = dictateService.findById((long)id);

        if(ObjectUtils.isEmpty(dictate)){
            return Constants.Redirect.DICTATE_ERROR;
        }

        NewDictateFrom newDictateFrom = new NewDictateFrom(dictate);
        model.addAttribute("dictate", newDictateFrom);
        return Constants.Views.VIEW_DICTATE;
    }

    @RequestMapping(value = Constants.Paths.DICTATE_CREATE, method = RequestMethod.GET)
    public String createDictate(Model model) {
        NewDictateFrom newDictateFrom = new NewDictateFrom();
        model.addAttribute("dictate", newDictateFrom);
        List<Language> languages = languageService.findAll();
        model.addAttribute("languages", languages);
        return Constants.Views.CREATE_DICTATE;
    }

    @RequestMapping(value = Constants.Paths.DICTATE_CREATE, method = RequestMethod.POST)
    public String createDictate(@ModelAttribute("dictate") @Valid NewDictateFrom dictateDto, BindingResult result, Model model, Errors errors) {
        if (result.hasErrors()) {
            model.addAttribute("dictate", dictateDto);
            List<Language> languages = languageService.findAll();
            model.addAttribute("languages", languages);
            return Constants.Views.CREATE_DICTATE;
        }

        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        try {
            if (ObjectUtils.isEmpty(user)) {
                throw new UserNotFoundException("User not found");
            }
            dictateService.addDictate(dictateDto, user);
        } catch (Throwable ex) {
            model.addAttribute("dictate", dictateDto);
            List<Language> languages = languageService.findAll();
            model.addAttribute("languages", languages);
            return Constants.Views.CREATE_DICTATE;
        }

        return Constants.Redirect.DICTATE_MY;
    }

    @RequestMapping(value = Constants.Paths.DICTATE_EDIT, method = RequestMethod.GET)
    public String editDictate(@RequestParam int id, Model model) {
        Dictate dictate = dictateService.findById((long)id);

        if(ObjectUtils.isEmpty(dictate)){
            return Constants.Redirect.DICTATE_ERROR;
        }

        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        if(!dictate.getUser().equals(user)){
            return Constants.Redirect.DICTATE_NO_AUTHORITY;
        }

        NewDictateFrom newDictateFrom = new NewDictateFrom(dictate);
        model.addAttribute("dictate", newDictateFrom);
        List<Language> languages = languageService.findAll();
        model.addAttribute("languages", languages);
        return Constants.Views.EDIT_DICTATE;
    }

    @RequestMapping(value = Constants.Paths.DICTATE_EDIT, method = RequestMethod.POST)
    public String editDictate(@ModelAttribute("dictate") @Valid NewDictateFrom dictateDto, BindingResult result, Model model, Errors errors) {
        if (result.hasErrors()) {
            model.addAttribute("dictate", dictateDto);
            List<Language> languages = languageService.findAll();
            model.addAttribute("languages", languages);
            return Constants.Views.EDIT_DICTATE;
        }

        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        try {
            if (ObjectUtils.isEmpty(user)) {
                throw new UserNotFoundException("User not found");
            }
            dictateService.editDictate(dictateDto, user);
        } catch (Throwable ex) {
            model.addAttribute("dictate", dictateDto);
            List<Language> languages = languageService.findAll();
            model.addAttribute("languages", languages);
            return Constants.Views.EDIT_DICTATE;
        }

        return Constants.Redirect.DICTATE_MY;
    }

    @RequestMapping(value = Constants.Paths.DICTATE_DELETE, method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse deleteDictate(@RequestParam("id") Long id) {
        User user;
        UserDetails userDetails;
        try {
            userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user = userService.findByEmail(userDetails.getUsername());
            if(user == null) throw new Exception();
        } catch (Exception ex) {
            return new GenericResponse("Error","You+don't+have+authority");
        }

        Dictate dictate  = dictateService.findById(id);
        if(dictate == null)
            return new GenericResponse("Error","Something+went+wrong");

        List<CorrectedDictation> correctedDictations = correctedDictationService.findByDictate(dictate);
        if(correctedDictations.size()>0)
            return new GenericResponse("Error","Can't+delete+dictate+because+there+exist+correction+that+needs+information+about+dictate");


        dictateService.deleteDictate(dictate);
        return new GenericResponse("Dictate deleted successfully");
    }

    @RequestMapping(value =Constants.Paths.DICTATE_UPLOAD_AUDIO, method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String url;
        try {
            url = storageService.uploadAudio(file);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return new GenericResponse("Error","Error while uploading file");
        }

        return new GenericResponse(url);
    }

}
