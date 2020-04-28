package hr.fer.genericframeworkforautomaticdictationcorrection.Forms;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Dictate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class NewDictateFrom {

    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String text;

    private String audioUrl;

    @NotNull
    @NotEmpty
    private String language;

    public NewDictateFrom(){

    }

    public NewDictateFrom(Dictate dictate){
        setId(dictate.getId());
        setName(dictate.getName());
        setAudioUrl(dictate.getAudioUrl());
        setText(dictate.getText());
        setLanguage(dictate.getLanguage().getCode());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
