package com.iscte.zoozone.models;

/**
 * Created by b.coitos on 9/26/2018.
 */

public class Language {

    private String initials;
    private String name;

    public Language() {
    }

    public Language(String initials, String name) {
        this.initials = initials;
        this.name = name;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}
