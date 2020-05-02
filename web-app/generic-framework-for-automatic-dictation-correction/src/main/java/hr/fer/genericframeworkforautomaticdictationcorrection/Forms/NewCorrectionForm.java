package hr.fer.genericframeworkforautomaticdictationcorrection.Forms;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.CorrectedDictation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class NewCorrectionForm {
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String urlOriginalImage;

    private String urlCorrectedImage;

    @NotNull
    @NotEmpty
    private String usedOCRMethod;

    private String detectedText;

    @NotNull
    @NotEmpty
    private String dictate;

    public NewCorrectionForm(){

    }

    public NewCorrectionForm(CorrectedDictation correctedDictation){
        setId(correctedDictation.getId());
        setName(correctedDictation.getName());
        setUrlCorrectedImage(correctedDictation.getUrlCorrectedImage());
        setUrlOriginalImage(correctedDictation.getUrlOriginalImage());
        setUsedOCRMethod(correctedDictation.getUsedOCRMethod());
        setDetectedText(correctedDictation.getDetectedText());
        setDictate(correctedDictation.getDictate().getName());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlOriginalImage() {
        return urlOriginalImage;
    }

    public void setUrlOriginalImage(String urlOriginalImage) {
        this.urlOriginalImage = urlOriginalImage;
    }

    public String getUrlCorrectedImage() {
        return urlCorrectedImage;
    }

    public void setUrlCorrectedImage(String urlCorrectedImage) {
        this.urlCorrectedImage = urlCorrectedImage;
    }

    public String getUsedOCRMethod() {
        return usedOCRMethod;
    }

    public void setUsedOCRMethod(String usedOCRMethod) {
        this.usedOCRMethod = usedOCRMethod;
    }

    public String getDetectedText() {
        return detectedText;
    }

    public void setDetectedText(String detectedText) {
        this.detectedText = detectedText;
    }

    public String getDictate() {
        return dictate;
    }

    public void setDictate(String dictate) {
        this.dictate = dictate;
    }
}