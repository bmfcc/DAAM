package com.iscte.zoozone.models;

/**
 * Created by b.coitos on 6/2/2018.
 */

public class History {

    private String title;
    private String text;

    public History(){}

    public History(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
