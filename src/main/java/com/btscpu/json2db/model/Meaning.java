package com.btscpu.json2db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Meaning {

    @JsonProperty("part_of_speech") // 映射 JSON 中的 part_of_speech 字段
    private String partOfSpeech;
    private String translation;
    private String explanation;
    private List<String> synonyms;

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }
}