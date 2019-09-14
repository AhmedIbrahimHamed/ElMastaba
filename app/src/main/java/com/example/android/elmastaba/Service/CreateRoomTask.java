package com.example.android.elmastaba.Service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.elmastaba.CreateRoomActivity;
import com.example.android.elmastaba.MainActivity;
import com.example.android.elmastaba.R;
import com.example.android.elmastaba.fragments.HomeFragment;
import com.example.android.elmastaba.models.AllRooms;
import com.example.android.elmastaba.models.ChatRoom;
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

import static com.example.android.elmastaba.fragments.HomeFragment.updateWidgets;

/**
 * Created by Ahmed on 8/7/2017.
 */

public class CreateRoomTask {


    public static void executeTask(Context context, String action,
                                   String name, String password, Uri photoUrl) {
        final String ACTION_CREATE_ROOM = context.getString(R.string.action_create_room_text);

        if (ACTION_CREATE_ROOM.equals(action)) {
            createRoom(context, name, password, photoUrl);
        }
    }

    private static void createRoom(final Context mContext, String roomName, String roomPassword, Uri mSelectedImage) {
        final FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child(mContext.getString(R.string.Users_text)).child(mFirebaseUser.getUid());
        final StorageReference mRoomsPhotosStorageRef = FirebaseStorage.getInstance().getReference().child(mContext.getString(R.string.rooms_photos_text)).child(roomName);
        //Creating new room object.
        final ChatRoom mChatRoom = new ChatRoom(roomName, mFirebaseUser.getUid());
        //Checking if there is a password for the room and add it to room if there's.
        if (! TextUtils.isEmpty(roomPassword)){
            mChatRoom.setmPassword(roomPassword);
        }
        //Checking if user uploaded a photo for the room if there's we add it to the room.
        if (!Uri.EMPTY.equals(mSelectedImage) && mSelectedImage != null) {
            StorageReference photoRef = mRoomsPhotosStorageRef.child(mSelectedImage.getLastPathSegment());
            photoRef.putFile(mSelectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri photoUriDownload = taskSnapshot.getDownloadUrl();
                    mChatRoom.setmPhotoUrl(photoUriDownload.toString());
                    mChatRoom.addUserToRoom(mFirebaseUser.getUid());
                    mUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User mUser = dataSnapshot.getValue(User.class);
                            roomWithPhoto(mContext,mUser, mChatRoom, mUserDatabaseRef);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }
            });

        }else {
            final DatabaseReference mChatRoomsRef = FirebaseDatabase.getInstance().getReference().child(mContext.getString(R.string.ChatRooms_text));
            mChatRoom.addUserToRoom(mFirebaseUser.getUid());
            mUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User mUser = dataSnapshot.getValue(User.class);
                    roomWithNoPhoto(mContext, mUser, mChatRoom, mUserDatabaseRef, mChatRoomsRef);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }

    }

    private static void roomWithPhoto(Context mContext,User mUser,final ChatRoom mChatRoom, DatabaseReference mUserDatabaseRef){
        mUser.addARoomToUserRooms(mChatRoom.getmName());
        mUserDatabaseRef.setValue(mUser);
        HomeFragment.updateWidgets(mContext);
        DatabaseReference mChatRoomsRef = FirebaseDatabase.getInstance().getReference().child(mContext.getString(R.string.ChatRooms_text));
        mChatRoomsRef.child(mChatRoom.getmName()).setValue(mChatRoom);
        final DatabaseReference mChatRoomNamesFirebaseRef = FirebaseDatabase.getInstance().getReference().child(mContext.getString(R.string.RoomNames_text));
        mChatRoomNamesFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AllRooms roomNames = dataSnapshot.getValue(AllRooms.class);
                if (roomNames == null){
                    roomNames = new AllRooms();
                }
                roomNames.addARoom(mChatRoom.getmName());
                mChatRoomNamesFirebaseRef.setValue(roomNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void roomWithNoPhoto(Context mContext,User mUser, final ChatRoom mChatRoom, DatabaseReference mUserDatabaseRef, DatabaseReference mChatRoomsRef){
        mUser.addARoomToUserRooms(mChatRoom.getmName());
        mUserDatabaseRef.setValue(mUser);
        HomeFragment.updateWidgets(mContext);
        mChatRoomsRef.child(mChatRoom.getmName()).setValue(mChatRoom);
        final DatabaseReference mChatRoomNamesFirebaseRef = FirebaseDatabase.getInstance().getReference().child(mContext.getString(R.string.RoomNames_text));
        mChatRoomNamesFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AllRooms roomNames = dataSnapshot.getValue(AllRooms.class);
                if (roomNames == null){
                    roomNames = new AllRooms();
                }
                roomNames.addARoom(mChatRoom.getmName());
                mChatRoomNamesFirebaseRef.setValue(roomNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
