package com.btscpu.json2db.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MusicSentence {

    @JsonProperty("song_name")
    private String song_name;
    private String singer;
    private String lyric;
    private String translation;

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}