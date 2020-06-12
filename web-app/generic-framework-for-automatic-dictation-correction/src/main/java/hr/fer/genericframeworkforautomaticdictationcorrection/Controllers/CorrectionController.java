package hr.fer.genericframeworkforautomaticdictationcorrection.Controllers;

import hr.fer.genericframeworkforautomaticdictationcorrection.Exceptions.OCRException;
import hr.fer.genericframeworkforautomaticdictationcorrection.Exceptions.UserNotFoundException;
import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.MultipleCorrectionsForm;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
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

        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findByEmail(userDetails.getUsername());

        /*if(!currentUser.getEmail().equals(correctedDictation.getUser().getEmail())){
            return Constants.Redirect.CORRECTION_ERROR;
        }*/

        model.addAttribute("correction", correctedDictation);
        return Constants.Views.VIEW_CORRECTION;
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
            String originalText= dictateService.findById(correctionDto.getDictateId()).getText();
            String htmlDiff = ocr.getHTMLDiff(originalText, detectedText);
            correctionDto.setHTMLDiff(htmlDiff);

            MultipartFile correctedImage=ocr.drawBoundBoxesForIncorrectWords(correctionDto.getUrlOriginalImage(), originalText, detectedText);

            String correctedUrl=null;
            if(correctedImage != null) //maybe method doesn't correct image
                correctedUrl=storageService.uploadImage(correctedImage);
            correctionDto.setUrlCorrectedImage(correctedUrl);
            correctedDictationService.addCorrection(correctionDto, user);
        }catch (OCRException ex){
            System.err.println(ex.getMessage());
            model.addAttribute("correction", correctionDto);
            List<Dictate> dictates = getDictates(user);
            model.addAttribute("dictates", dictates);
            List<String> OCRMethods = strategyFactory.getAllMethodesNames();
            model.addAttribute("OCRMethods", OCRMethods);
            result.rejectValue("usedOCRMethod", "error.usedOCRMethod", "Error! Use other OCR method");
            model.addAttribute("OCRError","error");
            return Constants.Views.CREATE_CORRECTION;
        } catch (Throwable ex) {
            System.err.println(ex.getMessage());
            model.addAttribute("correction", correctionDto);
            List<Dictate> dictates = getDictates(user);
            model.addAttribute("dictates", dictates);
            List<String> OCRMethods = strategyFactory.getAllMethodesNames();
            model.addAttribute("OCRMethods", OCRMethods);
            return Constants.Views.CREATE_CORRECTION;
        }

        return Constants.Redirect.CORRECTION_MY;
    }

    @RequestMapping(value = Constants.Paths.CORRECTION_CREATE_MULTIPLE, method = RequestMethod.GET)
    public String createMultiple(Model model) {
        MultipleCorrectionsForm  multipleCorrectionsForm = new MultipleCorrectionsForm();
        model.addAttribute("corrections", multipleCorrectionsForm );

        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        List<Dictate> dictates = getDictates(user);
        model.addAttribute("dictates", dictates);

        List<String> OCRMethods = strategyFactory.getAllMethodesNames();
        model.addAttribute("OCRMethods", OCRMethods);

        return Constants.Views.CREATE_MULTIPLE_CORRECTIONS;
    }

    @RequestMapping(value = Constants.Paths.CORRECTION_CREATE_MULTIPLE, method = RequestMethod.POST)
    public String createMultiple(@ModelAttribute("corrections") @Valid MultipleCorrectionsForm multipleDto, BindingResult result, Model model, Errors errors) {
        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        if (result.hasErrors()) {
            model.addAttribute("corrections", multipleDto);
            List<Dictate> dictates = getDictates(user);
            model.addAttribute("dictates", dictates);
            List<String> OCRMethods = strategyFactory.getAllMethodesNames();
            model.addAttribute("OCRMethods", OCRMethods);
            return Constants.Views.CREATE_MULTIPLE_CORRECTIONS;
        }

        try {
            if (ObjectUtils.isEmpty(user)) {
                throw new UserNotFoundException("User not found");
            }
            OCR ocr = strategyFactory.getMethod(multipleDto.getUsedOCRMethod());
            if(ObjectUtils.isEmpty(ocr)){
                System.err.println("OCR is null");
                throw new Exception("OCR is null");
            }

            List<String> urls = multipleDto.getUrlOriginalImages();
            List<String> names= multipleDto.getNames();
            int namesSize=names.size();

            List<NewCorrectionForm> corrections= new ArrayList<>();
            for(int i=0, len = urls.size();i<len;i++){
                NewCorrectionForm correctionDto = new NewCorrectionForm();
                String url=urls.get(i);
                correctionDto.setUrlOriginalImage(url);

                if(namesSize-1<i){
                    correctionDto.setName(java.time.LocalDateTime.now().toString());
                }else{
                    correctionDto.setName(names.get(i));
                }


                String detectedText = ocr.detectText(url);
                correctionDto.setDetectedText(detectedText);
                String originalText= dictateService.findById(correctionDto.getDictateId()).getText();
                String htmlDiff = ocr.getHTMLDiff(originalText, detectedText);
                correctionDto.setHTMLDiff(htmlDiff);

                correctionDto.setUsedOCRMethod(multipleDto.getUsedOCRMethod());
                correctionDto.setDictateId(multipleDto.getDictateId());

                MultipartFile correctedImage=ocr.drawBoundBoxesForIncorrectWords(correctionDto.getUrlOriginalImage(), originalText, detectedText);

                String correctedUrl=null;
                if(correctedImage != null) //maybe method doesn't correct image
                    correctedUrl=storageService.uploadImage(correctedImage);
                correctionDto.setUrlCorrectedImage(correctedUrl);

                corrections.add(correctionDto);
            }

            corrections.stream().forEach(c->correctedDictationService.addCorrection(c, user));
        }catch (OCRException ex){
            System.err.println(ex.getMessage());
            model.addAttribute("corrections", multipleDto);
            List<Dictate> dictates = getDictates(user);
            model.addAttribute("dictates", dictates);
            List<String> OCRMethods = strategyFactory.getAllMethodesNames();
            model.addAttribute("OCRMethods", OCRMethods);
            result.rejectValue("usedOCRMethod", "error.usedOCRMethod", "Error! Use other OCR method");
            model.addAttribute("OCRError","error");
            return Constants.Views.CREATE_MULTIPLE_CORRECTIONS;
        } catch (Throwable ex) {
            System.err.println(ex.getMessage());
            model.addAttribute("corrections", multipleDto);
            List<Dictate> dictates = getDictates(user);
            model.addAttribute("dictates", dictates);
            List<String> OCRMethods = strategyFactory.getAllMethodesNames();
            model.addAttribute("OCRMethods", OCRMethods);
            return Constants.Views.CREATE_MULTIPLE_CORRECTIONS;
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

    @RequestMapping(value = Constants.Paths.CORRECTION_UPLOAD_MULTIPLE, method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse uploadMultipleFiles(@RequestParam("file") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        try {
            for(MultipartFile file : files){
                 urls.add(storageService.uploadImage(file));
            }

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return new GenericResponse("Error","Error while uploading file(s)");
        }

        return new GenericResponse(urls.toString());
    }

    @RequestMapping(value = Constants.Paths.CORRECTION_DELETE, method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse deleteDictate(@RequestParam("id") Long id) {
        User user;
        UserDetails userDetails;
        try {
            userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user = userService.findByEmail(userDetails.getUsername());
            if(user == null) throw new Exception();
        } catch (Exception ex) {
            return new GenericResponse("Error","Security error");
        }

        CorrectedDictation correctedDictation  = correctedDictationService.findById(id);
        if(correctedDictation == null)
            return new GenericResponse("Error","Something+went+wrong");


        correctedDictationService.deleteCorrectedDictation(correctedDictation);
        return new GenericResponse("Correction deleted successfully");
    }

    @RequestMapping(value = Constants.Paths.CORRECTION_EDIT, method = RequestMethod.GET)
    public String editCorrection(@RequestParam int id, Model model) {
        CorrectedDictation correctedDictation = correctedDictationService.findById((long)id);
        NewCorrectionForm newCorrectionForm = new NewCorrectionForm(correctedDictation);

        if(ObjectUtils.isEmpty(correctedDictation)){
            return Constants.Redirect.CORRECTION_ERROR;
        }

        List<String> OCRMethods = strategyFactory.getAllMethodesNames();
        model.addAttribute("OCRMethods", OCRMethods);

        model.addAttribute("correction", newCorrectionForm);
        return Constants.Views.EDIT_CORRECTION;
    }

    @RequestMapping(value = Constants.Paths.CORRECTION_EDIT, method = RequestMethod.POST)
    public String editCorrection(@ModelAttribute("correction") @Valid NewCorrectionForm correctionDto, BindingResult result, Model model, Errors errors) {
        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        if (result.hasErrors()) {
            model.addAttribute("correction", correctionDto);
            List<String> OCRMethods = strategyFactory.getAllMethodesNames();
            model.addAttribute("OCRMethods", OCRMethods);
            return Constants.Views.EDIT_CORRECTION;
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
            String originalText= dictateService.findById(correctionDto.getDictateId()).getText();
            String htmlDiff = ocr.getHTMLDiff(originalText, detectedText);
            correctionDto.setHTMLDiff(htmlDiff);

            MultipartFile correctedImage=ocr.drawBoundBoxesForIncorrectWords(correctionDto.getUrlOriginalImage(), originalText, detectedText);

            String correctedUrl=null;
            if(correctedImage != null) //maybe method doesn't correct image
                correctedUrl=storageService.uploadImage(correctedImage);
            correctionDto.setUrlCorrectedImage(correctedUrl);
            correctedDictationService.editCorrection(correctionDto, user);
        }catch (OCRException ex){
            System.err.println(ex.getMessage());
            model.addAttribute("correction", correctionDto);
            List<String> OCRMethods = strategyFactory.getAllMethodesNames();
            model.addAttribute("OCRMethods", OCRMethods);
            result.rejectValue("usedOCRMethod", "error.usedOCRMethod", "Error! Use other OCR method");
            model.addAttribute("OCRError","error");
            return Constants.Views.EDIT_CORRECTION;
        } catch (Throwable ex) {
            System.err.println(ex.getMessage());
            model.addAttribute("correction", correctionDto);
            List<String> OCRMethods = strategyFactory.getAllMethodesNames();
            model.addAttribute("OCRMethods", OCRMethods);
            return Constants.Views.EDIT_CORRECTION;
        }

        return Constants.Redirect.CORRECTION_MY;
    }
}
