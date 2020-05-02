package hr.fer.genericframeworkforautomaticdictationcorrection.Controllers;

import hr.fer.genericframeworkforautomaticdictationcorrection.Exceptions.UserNotFoundException;
import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.NewCorrectionForm;
import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.NewDictateFrom;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.*;
import hr.fer.genericframeworkforautomaticdictationcorrection.OCR.OCR;
import hr.fer.genericframeworkforautomaticdictationcorrection.OCR.OCRStrategyFactory;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.*;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.GenericResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
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

    @Autowired
    OCRStrategyFactory strategyFactory;

    private Role teacher;


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
        return Constants.Views.VIEW_CORRECTIONS;
    }

    @RequestMapping(value = Constants.Paths.CORRECTION_CREATE, method = RequestMethod.GET)
    public String createDictate(Model model) {
        NewCorrectionForm newCorrectionForm = new NewCorrectionForm();
        model.addAttribute("correction", newCorrectionForm);

        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        List<Dictate> dictates = getDictates(user);
        model.addAttribute("dictates", dictates);

        List<String> OCRMethods = strategyFactory.getAllMethodesNames();
        model.addAttribute("OCRMethods", OCRMethods);

        return Constants.Views.CREATE_CORRECTION;
    }

    @RequestMapping(value = Constants.Paths.CORRECTION_CREATE, method = RequestMethod.POST)
    public String createDictate(@ModelAttribute("correction") @Valid NewCorrectionForm correctionDto, BindingResult result, Model model, Errors errors) {
        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        if (result.hasErrors()) {
            model.addAttribute("correction", correctionDto);
            List<Dictate> dictates = getDictates(user);
            model.addAttribute("dictates", dictates);
            List<String> OCRMethods = strategyFactory.getAllMethodesNames();
            model.addAttribute("OCRMethods", OCRMethods);
            return Constants.Views.CREATE_CORRECTION;
        }

        try {
            if (ObjectUtils.isEmpty(user)) {
                throw new UserNotFoundException("User not found");
            }
            OCR ocr = strategyFactory.getMethod(correctionDto.getUsedOCRMethod());
            if(ObjectUtils.isEmpty(ocr)){
                System.err.println("OCR is null");
                throw new Exception("OCR is null");
            }
            String detectedText = ocr.detectText(correctionDto.getUrlOriginalImage());
            correctionDto.setDetectedText(detectedText);
            //draw on image
            //call storage service for corrected image
            //set url...
            correctedDictationService.addCorrection(correctionDto, user);
        } catch (Throwable ex) {
            model.addAttribute("correction", correctionDto);
            List<Dictate> dictates = getDictates(user);
            model.addAttribute("dictates", dictates);
            List<String> OCRMethods = strategyFactory.getAllMethodesNames();
            model.addAttribute("OCRMethods", OCRMethods);
            return Constants.Views.CREATE_CORRECTION;
        }

        return Constants.Redirect.CORRECTION_MY;
    }

    private List<Dictate> getDictates(User user){
        if(ObjectUtils.isEmpty(user)){
            return new ArrayList<>();
        }

        teacher = roleService.findByName("ROLE_TEACHER");

        Collection<Role> roles = user.getRoles();
        List<Dictate> dictates;
        if(roles.contains(teacher)){
            dictates=dictateService.findAll();
        }else{
            dictates=dictateService.findByUser(user);
        }

        return dictates;
    }

    @RequestMapping(value = Constants.Paths.CORRECTION_UPLOAD_IMAGE, method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String url;
        try {
            url = storageService.uploadImage(file);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return new GenericResponse("Error","Error while uploading file");
        }

        return new GenericResponse(url);
    }
}
