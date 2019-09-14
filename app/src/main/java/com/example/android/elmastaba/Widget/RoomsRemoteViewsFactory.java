package com.example.android.elmastaba.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.elmastaba.R;
import com.example.android.elmastaba.models.AllRooms;
import com.example.android.elmastaba.models.ChatRoom;
import com.example.android.elmastaba.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ahmed on 8/8/2017.
 */

public class RoomsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, ValueEventListener {

    private ArrayList<String> mChatRooms = new ArrayList<>();;
    private Context mContext;

    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseRef;
    private DatabaseReference mChatRoomsNamesDatabaseRef;

    private String mUserId;


    public RoomsRemoteViewsFactory(Context context, Intent intent){
        this.mContext = context;
        this.mChatRooms = new ArrayList<>();

    }

    private void setChildListener(){
        mChatRooms.clear();

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserId = mFirebaseUser.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseRef = mFirebaseDatabase.getReference()
                .child(mContext.getResources().getString(R.string.Users_text)).child(mUserId);

        mUserDatabaseRef.addListenerForSingleValueEvent(this);

        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        setChildListener();
    }

    @Override
    public void onDestroy() {
        mChatRooms.clear();
    }

    @Override
    public int getCount() {
        return mChatRooms.size();
    }

    @Override
    public RemoteViews getViewAt(int pos) {
        RemoteViews remoteView = new RemoteViews(
                mContext.getPackageName(), R.layout.rooms_list_item);

        remoteView.setTextViewText(R.id.widget_list_item_room_name,mChatRooms.get(pos));

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User user= dataSnapshot.getValue(User.class);
        for (String roomName:user.getmChatRooms().keySet()){
            mChatRooms.add(roomName);
        }

        synchronized (this) {
            this.notify();
        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
