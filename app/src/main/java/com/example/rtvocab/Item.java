package com.example.rtvocab;

public class Item {

    private String first;
    private String second;

    public Item(String f, String s) {
        first = f;
        second = s;
    }
    public String getFirst() {
        return first;
    }
    public String getSecond() {
        return second;
    }

    public void setFirst(String f) {
        this.first = f;
    }
    public void setSecond(String s) { this.second = s; }


}