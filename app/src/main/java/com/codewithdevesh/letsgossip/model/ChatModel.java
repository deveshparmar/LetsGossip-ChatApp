package com.codewithdevesh.letsgossip.model;

public class ChatModel {
    private String sender;
    private String receiver;
    private String type;
    private String url;
    private String textMessage;
    private String dateTime;
    private Boolean isSeen;


    public ChatModel() {
    }

    public ChatModel(String sender, String receiver, String type, String url, String textMessage, String dateTime, Boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.url = url;
        this.textMessage = textMessage;
        this.dateTime = dateTime;
        this.isSeen = isSeen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getSeen() {
        return isSeen;
    }

    public void setSeen(Boolean seen) {
        isSeen = seen;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
