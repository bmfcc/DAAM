package com.iscte.zoozone.models;

import com.iscte.zoozone.SelectLanguage;

/**
 * Created by b.coitos on 10/1/2018.
 */

public class SelectLanguageInfo {

    private String confirmButton;
    private String selectLanguageMsg;

    public SelectLanguageInfo(){}

    public SelectLanguageInfo(String confirmButton, String selectLanguageMsg) {
        this.confirmButton = confirmButton;
        this.selectLanguageMsg = selectLanguageMsg;
    }

    public String getConfirmButton() {
        return confirmButton;
    }

    public void setConfirmButton(String confirmButton) {
        this.confirmButton = confirmButton;
    }

    public String getSelectLanguageMsg() {
        return selectLanguageMsg;
    }

    public void setSelectLanguageMsg(String selectLanguageMsg) {
        this.selectLanguageMsg = selectLanguageMsg;
    }
}
