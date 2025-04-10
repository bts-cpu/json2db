package com.btscpu.json2db.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Forms {

    private String plural;
    @JsonProperty("third_person_singular")
    private String third_person_singular;
    @JsonProperty("present_participle")
    private String present_participle;
    private String past;
    @JsonProperty("past_participle")
    private String past_participle;
    private String comparative;
    private String superlative;

    public String getPlural() {
        return plural;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    public String getThird_person_singular() {
        return third_person_singular;
    }

    public void setThird_person_singular(String third_person_singular) {
        this.third_person_singular = third_person_singular;
    }

    public String getPresent_participle() {
        return present_participle;
    }

    public void setPresent_participle(String present_participle) {
        this.present_participle = present_participle;
    }

    public String getPast() {
        return past;
    }

    public void setPast(String past) {
        this.past = past;
    }

    public String getPast_participle() {
        return past_participle;
    }

    public void setPast_participle(String past_participle) {
        this.past_participle = past_participle;
    }

    public String getComparative() {
        return comparative;
    }

    public void setComparative(String comparative) {
        this.comparative = comparative;
    }

    public String getSuperlative() {
        return superlative;
    }

    public void setSuperlative(String superlative) {
        this.superlative = superlative;
    }
}