package com.btscpu.json2db.model;

import java.util.List;

public class SpecialTranslation {

    private String major;
    private List<String> translations;

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public List<String> getTranslations() {
        return translations;
    }

    public void setTranslations(List<String> translations) {
        this.translations = translations;
    }
}