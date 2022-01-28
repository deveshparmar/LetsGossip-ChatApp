package com.codewithdevesh.letsgossip.model;

public class ChatListModel {
    private String usrId;
    private String usrName;
    private String description;
    private String date;
    private String urlProfile;

    public ChatListModel() {
    }

    public ChatListModel(String usrId, String usrName, String description, String date, String urlProfile) {
        this.usrId = usrId;
        this.usrName = usrName;
        this.description = description;
        this.date = date;
        this.urlProfile = urlProfile;
    }

    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }

    public String getUsrName() {
        return usrName;
    }

    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }
}
