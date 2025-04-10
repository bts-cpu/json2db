package com.btscpu.json2db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Word {
    private String word;
    @JsonProperty("phonetic_uk")
    private String phoneticUk;
    @JsonProperty("phonetic_us")
    private String phoneticUs;
    private Map<String, String> meanings;
    @JsonProperty("exam_types")
    private List<String> examTypes;
    private Map<String, String> forms;
    @JsonProperty("web_trans")
    private List<WebTranslation> webTrans;
    @JsonProperty("special_trans")
    private List<SpecialTranslation> specialTrans;
    @JsonProperty("ee_trans")
    private List<String> eeTrans;
    private List<String> phrs;
    private List<Example> examples;
    @JsonProperty("syno_phrases")
    private List<String> synoPhrases;
    private List<String> synonyms;
    @JsonProperty("similar_words")
    private List<String> similarWords;
    private List<String> etymology;
    @JsonProperty("auth_sents")
    private List<String> authSents;
    @JsonProperty("media_sents")
    private List<String> mediaSents;
}