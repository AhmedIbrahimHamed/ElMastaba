package com.example.android.elmastaba.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.elmastaba.R;
import com.example.android.elmastaba.models.Message;
import com.example.android.elmastaba.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ahmed on 8/7/2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private ArrayList<String> mMessages;
    private ArrayList<String> mNames;
    private ArrayList<String> mPhotos;
    private Context mContext;

    public MessagesAdapter(Context mContext) {
        this.mContext = mContext;
        this.mMessages = new ArrayList<>();
        this.mNames = new ArrayList<>();
        this.mPhotos = new ArrayList<>();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForMessage = R.layout.message_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForMessage, parent, false);

        MessageViewHolder messageViewHolder = new MessageViewHolder(view);

        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int position) {
        String message = mMessages.get(position);
        String userName = mNames.get(position);
        String photoUrl = mPhotos.get(position);

        if (!TextUtils.isEmpty(photoUrl)){
            Picasso.with(mContext)
                    .load(photoUrl)
                    .resize(360,360)
                    .into(viewHolder.mUserImage);
        }else {
            Picasso.with(mContext)
                    .load(R.mipmap.ic_person_black_with_background_green)
                    .resize(360,360)
                    .into(viewHolder.mUserImage);
        }
        viewHolder.mUserName.setText(userName);
        viewHolder.mMessageContent.setText(message);
    }

    @Override
    public int getItemCount() {
        if (mMessages == null){
            return 0;
        }
        return mMessages.size();
    }

    public void addMessage(String message,String name,String photoUrl){
        mMessages.add(message);
        mNames.add(name);
        mPhotos.add(photoUrl);
        notifyDataSetChanged();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView mUserImage;
        private TextView mUserName;
        private TextView mMessageContent;


        public MessageViewHolder(View view) {
            super(view);
            mUserImage = view.findViewById(R.id.message_user_image);
            mUserName = view.findViewById(R.id.message_user_name);
            mMessageContent = view.findViewById(R.id.message_content);
        }
    }

}
