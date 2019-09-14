package com.example.android.elmastaba;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.elmastaba.Adapter.MessagesAdapter;
import com.example.android.elmastaba.fragments.HomeFragment;
import com.example.android.elmastaba.models.AllRooms;
import com.example.android.elmastaba.models.ChatRoom;
import com.example.android.elmastaba.models.Message;
import com.example.android.elmastaba.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.id.message;

public class ChatRoomActivity extends AppCompatActivity {

    public static final int DEFAULT_MESSAGE_LENGTH_LIMIT = 1000;

    private RecyclerView mMessageRecyclerView;

    private LinearLayoutManager mLinearLayoutManger;

    private MessagesAdapter mAdapter;

    private DatabaseReference mRoomDatabaseRef;
    private DatabaseReference mRoomMessagesDatabaseRef;
    private DatabaseReference mUserDatabaseRef;
    private DatabaseReference mRoomNamesDatabaseRef;

    private AllRooms mRoomNames;

    private ChatRoom mRoom;

    private ChildEventListener mChildEventListener;

    private EditText mMessageEditText;
    private Button mSendMessageButton;

    String mUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        String roomName = getIntent().getStringExtra(getString(R.string.room_name_text));

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(roomName);


        mMessageRecyclerView = (RecyclerView) findViewById(R.id.message_rv);
        mLinearLayoutManger = new LinearLayoutManager(this);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManger);
        mMessageRecyclerView.setHasFixedSize(true);
        mAdapter = new MessagesAdapter(this);
        mMessageRecyclerView.setAdapter(mAdapter);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserID = firebaseUser.getUid();

        mRoomDatabaseRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.chat_room_text)).child(roomName);
        mRoomMessagesDatabaseRef = mRoomDatabaseRef.child(getResources().getString(R.string.mRoomMessages_text));
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.Users_text)).child(mUserID);
        mRoomNamesDatabaseRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.RoomNames_text));

        mRoomNamesDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRoomNames = dataSnapshot.getValue(AllRooms.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMessageEditText = (EditText) findViewById(R.id.chat_room_edit_text);
        mSendMessageButton = (Button) findViewById(R.id.chat_room_send_button);

        if (! SignInActivity.isNetworkAvailable(this)){
            Toast.makeText(ChatRoomActivity.this,
                    getString(R.string.internet_connection_unavailable_string),Toast.LENGTH_LONG).show();
        }

        addRoomListener();

        setMessageEditTextListener();

        setSendButtonListener();
    }

    private void addRoomListener(){
        if (mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    final Message message = dataSnapshot.getValue(Message.class);
                    String senderID = message.getmSender();
                    DatabaseReference senderDatabaseRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.Users_text)).child(senderID);
                    senderDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User sender = dataSnapshot.getValue(User.class);
                            String name = sender.getmName();
                            String photoUrl = sender.getmPhotoUrl();
                            mAdapter.addMessage(message.getmMessageContent(), name, photoUrl);
                            mLinearLayoutManger.smoothScrollToPosition(mMessageRecyclerView,null, mAdapter.getItemCount()-1);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mRoomMessagesDatabaseRef.addChildEventListener(mChildEventListener);
        }
    }

    private void setMessageEditTextListener(){
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendMessageButton.setEnabled(true);
                } else {
                    mSendMessageButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MESSAGE_LENGTH_LIMIT)});
    }

    private void setSendButtonListener(){
        if (! SignInActivity.isNetworkAvailable(this)){
            Toast.makeText(ChatRoomActivity.this,
                    getString(R.string.internet_connection_unavailable_string),Toast.LENGTH_LONG).show();
        }
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageContent = mMessageEditText.getText().toString().trim();
                if (! TextUtils.isEmpty(messageContent)){
                    Message message = new Message(mUserID, messageContent);
                    mRoomMessagesDatabaseRef.push().setValue(message);
                    mMessageEditText.setText("");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_room_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int selectedActionID= item.getItemId();
        switch (selectedActionID) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_leave_room:
                if (! SignInActivity.isNetworkAvailable(this)){
                    Toast.makeText(ChatRoomActivity.this,
                            getString(R.string.internet_connection_unavailable_string),Toast.LENGTH_LONG).show();
                    return true;
                }
                leaveRoom();
        }

        return true;
    }

    private void leaveRoom(){
        mRoomDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRoom = dataSnapshot.getValue(ChatRoom.class);

                mUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        mRoom.removeUserFromRoom(mUserID);
                        user.removeRoomFromUserRooms(mRoom.getmName());
                        if (mRoom.wasLastUserInRoom()){
                            mRoomDatabaseRef.removeValue();
                            mRoomNames.removeARoom(mRoom.getmName());
                            mRoomNamesDatabaseRef.setValue(mRoomNames);
                            mUserDatabaseRef.setValue(user);
                        } else if (user.getmID().equals(mRoom.getmAdmin())){
                            for (String userID : mRoom.getmRoomUsers().keySet()){
                                mRoom.setmAdmin(userID);
                                break;
                            }
                            mRoomDatabaseRef.setValue(mRoom);
                            mUserDatabaseRef.setValue(user);
                        }else {
                            mRoomDatabaseRef.setValue(mRoom);
                            mUserDatabaseRef.setValue(user);
                        }
                        HomeFragment.updateWidgets(getApplicationContext());
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
