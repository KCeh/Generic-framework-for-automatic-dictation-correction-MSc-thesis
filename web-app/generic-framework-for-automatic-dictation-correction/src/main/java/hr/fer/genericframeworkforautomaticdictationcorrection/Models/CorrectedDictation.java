package hr.fer.genericframeworkforautomaticdictationcorrection.Models;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "correctedDictation")
public class CorrectedDictation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    @Lob
    @Column( length = 100000 )
    private String urlOriginalImage;

    @Lob
    @Column( length = 100000 )
    private String urlCorrectedImage;

    @Lob
    @Column( length = 100000 )
    private String detectedText;

    @Lob
    @Column( length = 100000 )
    private String HTMLDiff;

    @NotEmpty
    private String usedOCRMethod;

    @ManyToOne
    @JoinColumn(name = "dictate_id", nullable = false)
    private Dictate dictate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
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

    public String getDetectedText() {
        return detectedText;
    }

    public void setDetectedText(String detectedText) {
        this.detectedText = detectedText;
    }

    public String getHTMLDiff() {
        return HTMLDiff;
    }

    public void setHTMLDiff(String HTMLDiff) {
        this.HTMLDiff = HTMLDiff;
    }

    public String getUsedOCRMethod() {
        return usedOCRMethod;
    }

    public void setUsedOCRMethod(String usedOCRMethod) {
        this.usedOCRMethod = usedOCRMethod;
    }

    public Dictate getDictate() {
        return dictate;
    }

    public void setDictate(Dictate dictate) {
        this.dictate = dictate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
