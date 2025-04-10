package com.btscpu.json2db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CollinsPrimary {

    @JsonProperty("part_of_speech")
    private String part_of_speech;
    private List<Definition> definitions;

    public String getPart_of_speech() {
        return part_of_speech;
    }

    public void setPart_of_speech(String part_of_speech) {
        this.part_of_speech = part_of_speech;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }

    public static class Definition {
        private String explanation;
        private List<String> examples;

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public List<String> getExamples() {
            return examples;
        }

        public void setExamples(List<String> examples) {
            this.examples = examples;
        }
    }
}
