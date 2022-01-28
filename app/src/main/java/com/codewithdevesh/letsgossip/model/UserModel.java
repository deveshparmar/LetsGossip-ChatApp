package com.codewithdevesh.letsgossip.model;

public class UserModel {
    String name;
    String bio;
    String phoneNo;
    String photoUri;

    public UserModel(){

    }

    public UserModel(String name, String bio, String phoneNo, String photoUri) {
        this.name = name;
        this.bio = bio;
        this.phoneNo = phoneNo;
        this.photoUri = photoUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
