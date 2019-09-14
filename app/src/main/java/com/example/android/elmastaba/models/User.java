package com.example.android.elmastaba.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ahmed on 8/2/2017.
 */

public class User {

    private String mName;
    private String mID;
    private String mBirthDay;
    private String mAddress;
    private String mPhotoUrl;
    private String mMobileNum;
    private HashMap<String, Boolean> mChatRooms = new HashMap<>();

    public User() {
        mChatRooms = new HashMap<>();
    }

    public User(String mName, String mID) {
        this.mName = mName;
        this.mID = mID;
        mChatRooms = new HashMap<>();
    }

    public User(String mName, String mID, String mBirthDay, String mAddress, String mMobileNum) {
        this.mName = mName;
        this.mID = mID;
        this.mBirthDay = String.valueOf(mBirthDay);
        this.mAddress = mAddress;
        this.mMobileNum = mMobileNum;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public void setmPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }

    public String getmBirthDay() {
        return mBirthDay;
    }

    public void setmBirthDay(String mBirthDay) {
        this.mBirthDay = mBirthDay;
    }

    public String getmMobileNum() {
        return mMobileNum;
    }

    public void setmMobileNum(String mMobileNum) {
        this.mMobileNum = mMobileNum;
    }

    public HashMap<String, Boolean> getmChatRooms() {
        return mChatRooms;
    }

    public void setmChatRooms(HashMap<String, Boolean> mChatRooms) {
        this.mChatRooms = mChatRooms;
    }

    public boolean isUserInRoom(String roomName){
        if (mChatRooms == null || mChatRooms.isEmpty()){
            return false;
        }
        return mChatRooms.containsKey(roomName);
    }

    public void addARoomToUserRooms(String roomName){
        if (mChatRooms == null){
            mChatRooms = new HashMap<>();
        }
        mChatRooms.put(roomName, true);
    }

    public void removeRoomFromUserRooms(String roomName){
        mChatRooms.remove(roomName);
    }

}
