package com.example.android.elmastaba.Service;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.example.android.elmastaba.R;
import com.example.android.elmastaba.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


/**
 * Created by Ahmed on 8/7/2017.
 */

public class UpdateProfileTask {


    public static void executeTask(Context mContext, String action, String userName,
                                   String address,String birthday,String mobile, Uri photoUrl) {
        final String ACTION_UPDATE_PROFILE = mContext.getString(R.string.action_update_profile_text);

        if (ACTION_UPDATE_PROFILE.equals(action)) {
            updateProfile(mContext, userName, address, birthday, mobile,photoUrl );
        }
    }

    public static void updateProfile(Context context, final String userName,
                                     final String address, final String birthday, final String mobile, final Uri mSelectedImage){
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final StorageReference mUsersPhotosStorageRef = FirebaseStorage.getInstance().getReference().child(context.getString(R.string.users_photos_text)).child(mFirebaseUser.getUid());
        final DatabaseReference mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.Users_text)).child(mFirebaseUser.getUid());
        mUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User mUser = dataSnapshot.getValue(User.class);
                mUser.setmName(userName);
                mUser.setmAddress(address);
                mUser.setmBirthDay(birthday);
                mUser.setmMobileNum(mobile);

                if (Uri.EMPTY.equals(mSelectedImage) || mSelectedImage == null || mSelectedImage.toString().equals(mUser.getmPhotoUrl())){
                    mUserDatabaseRef.setValue(mUser);

                }else {
                    StorageReference photoRef = mUsersPhotosStorageRef.child(mSelectedImage.getLastPathSegment());
                    photoRef.putFile(mSelectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri photoUriDownload = taskSnapshot.getDownloadUrl();
                            mUser.setmPhotoUrl(photoUriDownload.toString());
                            mUserDatabaseRef.setValue(mUser);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
