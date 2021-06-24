package com.example.rtvocab;

import android.content.SharedPreferences;

public class LanguagesPref {

    private String lanFrom;
    private String lanTo;
    private SharedPreferences sharedPreferences;
    String savedFrom;
    String savedTo;

    public LanguagesPref(SharedPreferences sharedPreferences, String savedFrom, String savedTo) {
        this.sharedPreferences = sharedPreferences;
        this.savedFrom = savedFrom;
        this.savedTo = savedTo;
        this.lanFrom = this.sharedPreferences.getString(savedFrom, "en");
        this.lanTo = this.sharedPreferences.getString(savedTo, "it");
    }

    public String getLanFrom() {
        return lanFrom;
    }

    public String getLanTo() {
        return lanTo;
    }

    public String getDictName() {
        return "Dict " + lanFrom + "-" + lanTo;
    }

    public void saveLanPref(String from, String to) {
        this.lanFrom = from;
        this.lanTo = to;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(savedFrom, from);
        editor.putString(savedTo, to);
        editor.commit();
    }
}
