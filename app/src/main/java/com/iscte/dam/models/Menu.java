package com.iscte.dam.models;

/**
 * Created by b.coitos on 6/2/2018.
 */

public class Menu {

    private String home;
    private String language;
    private String getMyZone;
    private String news;
    private String classify;
    private String report;
    private String communicate;

    public Menu() {}

    public Menu(String home, String language, String getMyZone, String news, String communicate, String classify, String report) {
        this.home = home;
        this.language = language;
        this.getMyZone = getMyZone;
        this.news = news;
        this.communicate = communicate;
        this.classify = classify;
        this.report = report;

    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGetMyZone() {
        return getMyZone;
    }

    public void setGetMyZone(String getMyZone) {
        this.getMyZone = getMyZone;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }


    public String getCommunicate() {
        return communicate;
    }

    public void setCommunicate(String communicate) {
        this.communicate = communicate;
    }

}
