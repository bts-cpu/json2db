package com.btscpu.json2db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Synonym {

    @JsonProperty("part_of_speech")
    private String part_of_speech;
    private List<String> words;
    private String translation;

    public String getPart_of_speech() {
        return part_of_speech;
    }

    public void setPart_of_speech(String part_of_speech) {
        this.part_of_speech = part_of_speech;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}