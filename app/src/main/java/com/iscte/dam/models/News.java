package com.iscte.dam.models;

/**
 * Created by b.coitos on 5/31/2018.
 */

public class News {

    private String title;
    private String shortDescription;
    private String description;

    public News(){}

    public News(String title, String shortDescription, String description){
        this.title=title;
        this.shortDescription=shortDescription;
        this.description=description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
