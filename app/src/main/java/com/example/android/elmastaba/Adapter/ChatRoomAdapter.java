package com.example.android.elmastaba.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.elmastaba.R;
import com.example.android.elmastaba.models.ChatRoom;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ahmed on 8/4/2017.
 */

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {

    private ArrayList<ChatRoom> mChatRooms;
    private Context mContext;

    public ChatRoomAdapter(Context mContext) {
        this.mContext = mContext;
        this.mChatRooms = new ArrayList<>();
    }

    @Override
    public ChatRoomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForPhoto = R.layout.chat_room_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForPhoto, viewGroup, false);

        ChatRoomViewHolder chatRoomViewHolder = new ChatRoomViewHolder(view);

        return chatRoomViewHolder;
    }

    @Override
    public void onBindViewHolder(ChatRoomViewHolder viewHolder, int position) {
        ChatRoom mRoom = mChatRooms.get(position);

        String mRoomName = mRoom.getmName();
        String mRoomPassword = mRoom.getmPassword();
        String mRoomImage = mRoom.getmPhotoUrl();

        viewHolder.mChatRoomName.setTag(mRoomName);

        viewHolder.mChatRoomName.setText(mRoomName);
        if (!TextUtils.isEmpty(mRoomPassword)){
            viewHolder.mChatRoomPassword.setVisibility(View.VISIBLE);
        }
        if (mRoomImage != null  && !TextUtils.isEmpty(mRoomImage)){
            Picasso.with(mContext)
                    .load(mRoomImage)
                    .resize(96,96)
                    .into(viewHolder.mChatRoomImage);
        }

    }

    @Override
    public int getItemCount() {
        if (mChatRooms == null){
            return 0;
        }
        return mChatRooms.size();
    }

    public void addAChatRoom(ChatRoom chatRoom){
        mChatRooms.add(chatRoom);
        notifyDataSetChanged();
    }

    public void removeChatRoom(ChatRoom chatRoom){
        for (int i=0 ; i<mChatRooms.size(); i++){
            if (chatRoom.getmName().equals(mChatRooms.get(i).getmName())){
                mChatRooms.remove(i);
                notifyDataSetChanged();
                break;
            }
        }

    }

    public String getRoomName(int pos){
        return mChatRooms.get(pos).getmName();
    }


    public class ChatRoomViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView mChatRoomImage;
        private TextView mChatRoomName;
        private ImageView mChatRoomPassword;

        public ChatRoomViewHolder(View view) {
            super(view);
            mChatRoomImage = (CircleImageView)  view.findViewById(R.id.chat_room_item_image);
            mChatRoomName = (TextView) view.findViewById(R.id.chat_room_item_name);
            mChatRoomPassword = (ImageView) view.findViewById(R.id.chat_room_item_password);
        }
    }
}
