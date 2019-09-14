package com.example.android.elmastaba.models;

import java.util.HashMap;

/**
 * Created by Ahmed on 8/3/2017.
 */

public class AllRooms {

    private HashMap<String, Boolean> mRoomNames;

    public AllRooms() {
        this.mRoomNames = new HashMap<>();
    }

    public AllRooms(HashMap<String,Boolean> roomNames) {
        this.mRoomNames = roomNames;
    }

    public HashMap<String,Boolean> getRoomNames() {
        return mRoomNames;
    }

    public void setRoomNames(HashMap<String,Boolean> roomNames) {
        this.mRoomNames = roomNames;
    }

    public void addARoom(String roomName){
        mRoomNames.put(roomName,true);
    }

    public void removeARoom(String roomName){
        mRoomNames.remove(roomName);
    }

    public boolean hasRoomName(String roomName){
        if (mRoomNames != null && !mRoomNames.isEmpty()){
            return mRoomNames.containsKey(roomName);
        }
        return false;
    }
}
