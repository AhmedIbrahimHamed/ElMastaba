package com.example.android.elmastaba.fragments;


import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.elmastaba.Adapter.ChatRoomAdapter;
import com.example.android.elmastaba.Adapter.RecyclerItemClickListener;
import com.example.android.elmastaba.ChatRoomActivity;
import com.example.android.elmastaba.CreateRoomActivity;
import com.example.android.elmastaba.MainActivity;
import com.example.android.elmastaba.R;
import com.example.android.elmastaba.SignInActivity;
import com.example.android.elmastaba.Widget.RoomsWidget;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.R.attr.offset;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private Context mContext;

    private RecyclerView mRecyclerView;                 //Recycler view for photos in galleries.

    private GridLayoutManager mGridLayoutManager;       //Layout manger that will handle how the recycler will be displayed.

    private ChatRoomAdapter mChatRoomAdapter;           //Recycler view adapter;

    private DatabaseReference mChatRoomsDatabaseRef,    //Firebase database Reference to all chat rooms.
            mChatRoomDatabaseRef;                       //Firebase database Reference to chat room clicked.

    private ChildEventListener mChildEventListener;     //Listener Used to get data from the database.

    private String mUserID;

    private FloatingActionButton mCreateRoomFAB;

    private boolean hasAttachedToActivity = false;

    private Set<String> mRoomNames;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        hasAttachedToActivity = true;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getContext();

        getActivity().setTitle(getString(R.string.nav_home));
        setHasOptionsMenu(true);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        mChatRoomsDatabaseRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ChatRooms_text));

        if (firebaseUser != null){
            mUserID = firebaseUser.getUid();
        }

        mRoomNames = new HashSet<>();

        mRecyclerView = rootView.findViewById(R.id.home_rv);
        mGridLayoutManager = new GridLayoutManager(mContext,
                getResources().getInteger(R.integer.chat_rooms_grid_layout_column_num));
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mChatRoomAdapter = new ChatRoomAdapter(mContext);
        mRecyclerView.setAdapter(mChatRoomAdapter);
        setFirebaseListener(savedInstanceState);
        addRecyclerViewClickListener();

        mCreateRoomFAB = (FloatingActionButton) rootView.findViewById(R.id.create_room_fab);
        mCreateRoomFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startCreateRoomIntent = new Intent(mContext, CreateRoomActivity.class);
                startActivity(startCreateRoomIntent);
            }
        });

        return rootView;
    }

    public void setFirebaseListener(final Bundle savedInstanceState){
        //assign a listener to the database if there's non
        if (mChildEventListener == null){
            {
               mChildEventListener = new ChildEventListener() {
                   @Override
                   public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                       ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                       mRoomNames.add(chatRoom.getmName());
                       mChatRoomAdapter.addAChatRoom(chatRoom);
                       if (savedInstanceState != null && hasAttachedToActivity){
                           mRecyclerView.scrollToPosition(savedInstanceState.getInt(getResources().getString(R.string.scroll_position_text)));
                       }
                   }

                   @Override
                   public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                   }

                   @Override
                   public void onChildRemoved(DataSnapshot dataSnapshot) {
                       ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                       mRoomNames.remove(chatRoom.getmName());
                       mChatRoomAdapter.removeChatRoom(chatRoom);
                   }

                   @Override
                   public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               };
            }
            mChatRoomsDatabaseRef.addChildEventListener(mChildEventListener);
        }

    }
    private void addRecyclerViewClickListener(){
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final String strRoomName = mChatRoomAdapter.getRoomName(position);
                mChatRoomDatabaseRef = mChatRoomsDatabaseRef.child(strRoomName);

                enterRoom();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mChatRoomsDatabaseRef.removeEventListener(mChildEventListener);
    }

    private void enterRoom(){
        mChatRoomDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                if (! chatRoom.roomHasUser(mUserID)){

                    if (chatRoom.roomHasPassword()){
                        roomPasswordCorrect(chatRoom);

                    }else {
                        userAddRoomAndRoomAddUser(chatRoom);
                    }

                }else {
                    startChatActivity(chatRoom.getmName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void roomPasswordCorrect(final ChatRoom chatRoom){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.password_prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        // set prompts.xml to alert dialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userPasswordInput = promptsView.findViewById(R.id.password_prompts_edit_Text_);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_positive_value),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user password and compare it is the same password as the room.
                                String userEnteredPassword = userPasswordInput.getText().toString().trim();
                                if (chatRoom.getmPassword().equals(userEnteredPassword)){
                                    userAddRoomAndRoomAddUser(chatRoom);
                                }else {
                                    Toast.makeText(mContext,
                                            getString(R.string.chat_room_password_wrong_message),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton(getString(R.string.dialog_negative_value),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    alertDialog.dismiss();
                }
                return false;
            }
        });

        // show it
        alertDialog.show();

    }

    public void userAddRoomAndRoomAddUser(final ChatRoom chatRoom){
        final DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.Users_text)).child(mUserID);
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.addARoomToUserRooms(chatRoom.getmName());
                chatRoom.addUserToRoom(mUserID);
                mChatRoomDatabaseRef.setValue(chatRoom);
                mUserRef.setValue(user);
                updateWidgets(mContext);
                startChatActivity(chatRoom.getmName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void startChatActivity(String roomName){
        Intent intent = new Intent(mContext, ChatRoomActivity.class);
        intent.putExtra(getString(R.string.room_name_text), roomName);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedActionID= item.getItemId();
        switch (selectedActionID) {
            case R.id.action_search:
                userSearchRoom();
                break;
        }
        return true;
    }

    private void userSearchRoom(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.search_prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        // set prompts.xml to alert dialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput =  promptsView.findViewById(R.id.search_prompts_edit_Text_);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_positive_value),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user password and compare it is the same password as the room.
                                String userEnteredRoomName = userInput.getText().toString().trim();
                                if (mRoomNames != null && mRoomNames.contains(userEnteredRoomName)){
                                    Toast.makeText(mContext,
                                            getString(R.string.search_room_found_message),
                                            Toast.LENGTH_LONG).show();
                                    startChatActivity(userEnteredRoomName);
                                }else {
                                    Toast.makeText(mContext,
                                            getString(R.string.search_room_not_fount_message),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton(getString(R.string.dialog_negative_value),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    alertDialog.dismiss();
                }
                return false;
            }
        });

        // show it
        alertDialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        int scrollPosition = mGridLayoutManager.findFirstVisibleItemPosition();
        outState.putInt(getString(R.string.scroll_position_text),scrollPosition);
        super.onSaveInstanceState(outState);
    }

    public static void updateWidgets(Context mContext) {
        ComponentName name = new ComponentName(mContext, RoomsWidget.class);
        int[] ids = AppWidgetManager.getInstance(mContext).getAppWidgetIds(name);
        Intent intent = new Intent(mContext, RoomsWidget.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent.putExtra(AppWidgetManager.ACTION_APPWIDGET_UPDATE, ids);
        mContext.sendBroadcast(intent);
    }
}
