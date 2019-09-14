package com.example.android.elmastaba.models;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ahmed on 8/2/2017.
 */

public class ChatRoom implements Serializable {

    private String mName;
    private String mAdmin;
    private String mPassword;
    private String mPhotoUrl;
    private HashMap<String, Boolean> mRoomUsers = new HashMap<>();
    private HashMap<String, Message> mRoomMessages = new HashMap<>();

    public ChatRoom() {
        mRoomUsers = new HashMap<>();
        mRoomMessages = new HashMap<>();
    }

    public ChatRoom(String mName, String mAdmin) {
        this.mName = mName;
        this.mAdmin = mAdmin;
        this.mRoomUsers = new HashMap<>();
        this.mRoomMessages = new HashMap<>();
    }


    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmAdmin() {
        return mAdmin;
    }

    public void setmAdmin(String mAdmin) {
        this.mAdmin = mAdmin;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public void setmPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }

    public HashMap<String, Boolean> getmRoomUsers() {
        return mRoomUsers;
    }

    public void setmRoomUsers(HashMap<String, Boolean> mRoomUsers) {
        this.mRoomUsers = mRoomUsers;
    }

    public HashMap<String, Message> getmRoomMessages() {
        return mRoomMessages;
    }

    public void setmRoomMessages(HashMap<String, Message> mRoomMessages) {
        this.mRoomMessages = mRoomMessages;
    }

    public boolean roomHasUser(String userID){
        if (mRoomUsers == null || mRoomUsers.isEmpty()){
            return false;
        }
        return mRoomUsers.containsKey(userID);
    }

    public boolean roomHasPassword(){
        if (mPassword == null){
            return false;
        }
        return (mPassword.length() > 0);
    }

    public void addUserToRoom(String userID){
        if (mRoomUsers == null){
            mRoomUsers = new HashMap<>();
        }
        mRoomUsers.put(userID, true);
    }

    public boolean wasLastUserInRoom(){
        if (mRoomUsers == null){
            return true;
        }
        return  mRoomUsers.isEmpty();
    }

    public void removeUserFromRoom(String userID){
        mRoomUsers.remove(userID);
    }


}
