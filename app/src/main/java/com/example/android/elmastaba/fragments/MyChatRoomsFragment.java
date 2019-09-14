package com.example.android.elmastaba.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.elmastaba.Adapter.ChatRoomAdapter;
import com.example.android.elmastaba.Adapter.RecyclerItemClickListener;
import com.example.android.elmastaba.ChatRoomActivity;
import com.example.android.elmastaba.R;
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
import java.util.HashMap;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyChatRoomsFragment extends Fragment {

    private Context mContext;

    private RecyclerView mRecyclerView;                 //Recycler view for photos in galleries.

    private ChatRoomAdapter mChatRoomAdapter;           //Recycler view adapter;

    private GridLayoutManager mGridLayoutManager;

    private DatabaseReference mUserDatabaseRef;         //Firebase database Reference to user.

    private String mUserID;

    private ArrayList<ChatRoom>  mChatRoomsRecycler;

    public MyChatRoomsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_chat_rooms, container, false);
        mContext = getContext();

        getActivity().setTitle(getString(R.string.nav_my_chat_rooms));

        mRecyclerView = rootView.findViewById(R.id.my_chat_rooms_rv);
        mGridLayoutManager = new GridLayoutManager(mContext,
                getResources().getInteger(R.integer.chat_rooms_grid_layout_column_num));

        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mChatRoomAdapter = new ChatRoomAdapter(mContext);

        mChatRoomsRecycler = new ArrayList<>();

        mRecyclerView.setAdapter(mChatRoomAdapter);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.Users_text)).child(firebaseUser.getUid());

        addRoomsToAdapter(savedInstanceState);

        addListenerForRecycler();

        return rootView;
    }

    public void addRoomsToAdapter(final Bundle saveInstanceState){
        mUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                DatabaseReference mChatRoomsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ChatRooms_text));
                mChatRoomsRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                        if (user.isUserInRoom(chatRoom.getmName())){
                            mChatRoomAdapter.addAChatRoom(chatRoom);
                            if (saveInstanceState != null){
                                mRecyclerView.scrollToPosition(saveInstanceState.getInt(getString(R.string.scroll_position_text)));
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                        mChatRoomAdapter.removeChatRoom(chatRoom);

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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


    public void addListenerForRecycler(){
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final String strRoomName = mChatRoomAdapter.getRoomName(position);
                Intent intent = new Intent(mContext, ChatRoomActivity.class);
                intent.putExtra(getString(R.string.room_name_text),strRoomName);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        int scrollPosition = mGridLayoutManager.findFirstVisibleItemPosition();
        outState.putInt(getString(R.string.scroll_position_text),scrollPosition);
        super.onSaveInstanceState(outState);
    }
}
