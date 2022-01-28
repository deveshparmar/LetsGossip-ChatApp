package com.codewithdevesh.letsgossip.model;

import java.util.ArrayList;

public class StatusModel {
    private String name;
    private String time;
    private ArrayList<String>statuses;

    public StatusModel() {
    }

    public StatusModel(String name, String time, ArrayList<String> statuses) {
        this.name = name;
        this.time = time;
        this.statuses = statuses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<String> statuses) {
        this.statuses = statuses;
    }
}
