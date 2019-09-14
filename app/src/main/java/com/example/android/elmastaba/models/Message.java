package com.example.android.elmastaba.models;

import java.io.Serializable;

/**
 * Created by Ahmed on 8/2/2017.
 */

public class Message implements Serializable {

    private String mSender;             //Contains the sender is username.
    private String mMessageContent;     //Contains the content of the message.

    public Message(){

    }

    //Constructor.
    public Message(String mSender, String mMessageContent) {
        this.mSender = mSender;
        this.mMessageContent = mMessageContent;
    }

    //Getters and setters.
    public String getmSender() {
        return mSender;
    }

    public void setmSender(String mSender) {
        this.mSender = mSender;
    }

    public String getmMessageContent() {
        return mMessageContent;
    }

    public void setmMessageContent(String mMessageContent) {
        this.mMessageContent = mMessageContent;
    }
}
