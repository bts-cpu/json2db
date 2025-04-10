package com.btscpu.json2db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Word {

    private String word;
    private Phonetics phonetics;
    private List<Meaning> meanings;
    private Forms forms;
    @JsonProperty("exam_types")
    private List<String> exam_types;
    private List<Example> examples;
    @JsonProperty("web_translations")
    private List<WebTranslation> web_translations;
    @JsonProperty("special_translations")
    private List<SpecialTranslation> special_translations;
    private List<Phrase> phrases;
    private List<Synonym> synonyms;
    @JsonProperty("related_words")
    private List<RelatedWord> related_words;
    @JsonProperty("authoritative_sentences")
    private List<String> authoritative_sentences;
    @JsonProperty("media_sentences")
    private List<String> media_sentences;
    private Etymology etymology;
    private List<Collins> collins;
    @JsonProperty("collins_primary")
    private List<CollinsPrimary> collins_primary;
    @JsonProperty("wikipedia_digest")
    private String wikipedia_digest;
    @JsonProperty("music_sentences")
    private List<MusicSentence> music_sentences;

    // Getters and Setters
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Phonetics getPhonetics() {
        return phonetics;
    }

    public void setPhonetics(Phonetics phonetics) {
        this.phonetics = phonetics;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }

    public Forms getForms() {
        return forms;
    }

    public void setForms(Forms forms) {
        this.forms = forms;
    }

    public List<String> getExam_types() {
        return exam_types;
    }

    public void setExam_types(List<String> exam_types) {
        this.exam_types = exam_types;
    }

    public List<Example> getExamples() {
        return examples;
    }

    public void setExamples(List<Example> examples) {
        this.examples = examples;
    }

    public List<WebTranslation> getWeb_translations() {
        return web_translations;
    }

    public void setWeb_translations(List<WebTranslation> web_translations) {
        this.web_translations = web_translations;
    }

    public List<SpecialTranslation> getSpecial_translations() {
        return special_translations;
    }

    public void setSpecial_translations(List<SpecialTranslation> special_translations) {
        this.special_translations = special_translations;
    }

    public List<Phrase> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<Phrase> phrases) {
        this.phrases = phrases;
    }

    public List<Synonym> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<Synonym> synonyms) {
        this.synonyms = synonyms;
    }

    public List<RelatedWord> getRelated_words() {
        return related_words;
    }

    public void setRelated_words(List<RelatedWord> related_words) {
        this.related_words = related_words;
    }

    public List<String> getAuthoritative_sentences() {
        return authoritative_sentences;
    }

    public void setAuthoritative_sentences(List<String> authoritative_sentences) {
        this.authoritative_sentences = authoritative_sentences;
    }

    public List<String> getMedia_sentences() {
        return media_sentences;
    }

    public void setMedia_sentences(List<String> media_sentences) {
        this.media_sentences = media_sentences;
    }

    public Etymology getEtymology() {
        return etymology;
    }

    public void setEtymology(Etymology etymology) {
        this.etymology = etymology;
    }

    public List<Collins> getCollins() {
        return collins;
    }

    public void setCollins(List<Collins> collins) {
        this.collins = collins;
    }

    public List<CollinsPrimary> getCollins_primary() {
        return collins_primary;
    }

    public void setCollins_primary(List<CollinsPrimary> collins_primary) {
        this.collins_primary = collins_primary;
    }

    public String getWikipedia_digest() {
        return wikipedia_digest;
    }

    public void setWikipedia_digest(String wikipedia_digest) {
        this.wikipedia_digest = wikipedia_digest;
    }

    public List<MusicSentence> getMusic_sentences() {
        return music_sentences;
    }

    public void setMusic_sentences(List<MusicSentence> music_sentences) {
        this.music_sentences = music_sentences;
    }
}
