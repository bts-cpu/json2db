package com.btscpu.json2db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RelatedWord {

    @JsonProperty("part_of_speech")
    private String part_of_speech;
    private List<WordEntry> words;

    public String getPart_of_speech() {
        return part_of_speech;
    }

    public void setPart_of_speech(String part_of_speech) {
        this.part_of_speech = part_of_speech;
    }

    public List<WordEntry> getWords() {
        return words;
    }

    public void setWords(List<WordEntry> words) {
        this.words = words;
    }

    public static class WordEntry {
        private String word;
        private String translation;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getTranslation() {
            return translation;
        }

        public void setTranslation(String translation) {
            this.translation = translation;
        }
    }
}
