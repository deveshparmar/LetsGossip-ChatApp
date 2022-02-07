package com.codewithdevesh.letsgossip.model;

public class RecentChatModel {
    String lastMessage;
    String date;
    String time;
    String name;
    String profile;
    String userId;

    public RecentChatModel() {
    }

    public RecentChatModel(String lastMessage, String date,String time, String name, String profile, String userId) {
        this.lastMessage = lastMessage;
        this.date = date;
        this.name = name;
        this.profile = profile;
        this.userId = userId;
        this.time=time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
