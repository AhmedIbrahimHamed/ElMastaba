package com.example.android.elmastaba.Service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;

import com.example.android.elmastaba.R;


public class StorageFirebaseService extends IntentService {

    public StorageFirebaseService() {
        super(StorageFirebaseService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if (action.equals(getString(R.string.action_create_room))){
            String roomName = intent.getStringExtra(getResources().getString(R.string.room_name_extra_text));
            String roomPassword = intent.getStringExtra(getResources().getString(R.string.room_password_extra_text));
            Uri photoUrl = intent.getParcelableExtra(getResources().getString(R.string.room_photo_extra_text));

            CreateRoomTask.executeTask(this, action, roomName, roomPassword, photoUrl);
        }else if (action.equals(getString(R.string.action_update_profile))){
            String userName = intent.getStringExtra(getString(R.string.user_name_extra_text));
            String address = intent.getStringExtra(getString(R.string.address_extra_text));
            String birthday = intent.getStringExtra(getString(R.string.birth_day_extra_text));
            String mobile = intent.getStringExtra(getString(R.string.mobile_extra_text));
            Uri photo = intent.getParcelableExtra(getString(R.string.photo_extra_text));

            UpdateProfileTask.executeTask(this, action, userName, address, birthday, mobile, photo);
        }

    }


}
