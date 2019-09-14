package com.example.android.elmastaba;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.elmastaba.Service.StorageFirebaseService;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.R.attr.bitmap;
import static android.R.attr.password;
import static java.security.AccessController.getContext;

public class CreateRoomActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageButton mRoomImageButton;
    private EditText mRoomNameEditText;
    private EditText mRoomPasswordEditText;
    private Button mRoomSaveButton;

    private Uri mSelectedImage;

    private AllRooms mRoomsNames;

    private boolean validName;
    private boolean validPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.create_room_activity_title));

        mRoomImageButton = (ImageButton) findViewById(R.id.create_room_image);
        mRoomNameEditText = (EditText) findViewById(R.id.create_room_name_edit_text);
        mRoomPasswordEditText = (EditText) findViewById(R.id.create_room_password_edit_text);

        DatabaseReference mChatRoomNamesRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.RoomNames_text));
        mChatRoomNamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               mRoomsNames = dataSnapshot.getValue(AllRooms.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        //Load data when rotate.
        if (savedInstanceState != null){
            mRoomNameEditText.setText(savedInstanceState.getString(getString(R.string.room_name_text)));
            mRoomPasswordEditText.setText(savedInstanceState.getString(getString(R.string.room_password_text)));
            mSelectedImage = savedInstanceState.getParcelable(getString(R.string.room_image_text));
            if (!Uri.EMPTY.equals(mSelectedImage) && mSelectedImage != null){
                Picasso.with(this)
                        .load(mSelectedImage)
                        .resize(360,360)
                        .into(mRoomImageButton);
            }
        }

        mRoomSaveButton = (Button) findViewById(R.id.create_room_save_button);
        //Set a click listener for the image button to Start an Image Chooser.
        onImageButtonClicked();
        //Set a click listener for save button to valid the data then save it on firebase.
        onSaveButtonPressed();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mSelectedImage = data.getData();

            Picasso.with(this)
                    .load(mSelectedImage)
                    .resize(360,360)
                    .into(mRoomImageButton);
        }
    }


    public void onImageButtonClicked(){
        mRoomImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType(getString(R.string.all_image_types));
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_text)), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void onSaveButtonPressed(){

        mRoomSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if network available.
                if(!SignInActivity.isNetworkAvailable(CreateRoomActivity.this)){
                    Toast.makeText(CreateRoomActivity.this, getString(R.string.internet_connection_unavailable_string), Toast.LENGTH_LONG).show();
                    return;
                }
                //initializing the boolean varbs that we will use to test validation of data.
                validName = true;
                validPassword = true;

                final String roomName = mRoomNameEditText.getText().toString().trim();
                //validate room name.
                if (roomName.isEmpty()) {
                    mRoomNameEditText.setError(getString(R.string.required_string));
                    validName = false;
                } else if (roomName.length() < 3) {
                    mRoomNameEditText.setError(getString(R.string.create_room_name_validation_error));
                    validName = false;
                }
                int length = roomName.length();
                for (int i=0; i<length ;i++){
                    char ch = roomName.charAt(i);
                    if ( !Character.isDigit(ch) && !Character.isLetter(ch) && ch == ' ' && ch !='_') {
                        mRoomNameEditText.setError(getString(R.string.create_room_name_validation_characters_error));
                        validName = false;
                        break;
                    }
                }
                if (mRoomsNames != null){
                    if (mRoomsNames.hasRoomName(roomName)) {
                        mRoomNameEditText.setError(getString(R.string.create_room_name_validation_used_name_error));
                        validName = false;
                    }
                }

                //validate room password.
                String roomPassword = mRoomPasswordEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(roomPassword)){
                    if (roomPassword.length() < 4) {
                        mRoomPasswordEditText.setError(getString(R.string.create_room_password_validation_error));
                        validPassword = false;
                    }
                }

                if (validName && validPassword){
                    Intent intent = new Intent(CreateRoomActivity.this, StorageFirebaseService.class);
                    intent.setAction(getString(R.string.action_create_room));
                    intent.putExtra(getString(R.string.room_name_extra_text),roomName);
                    intent.putExtra(getString(R.string.room_password_extra_text),roomPassword);
                    intent.putExtra(getString(R.string.room_photo_extra_text),mSelectedImage);
                    startService(intent);
                    Toast.makeText(CreateRoomActivity.this, getString(R.string.create_room_waiting_message), Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(getString(R.string.room_name_text), mRoomNameEditText.getText().toString());
        outState.putString(getString(R.string.room_password_text), mRoomPasswordEditText.getText().toString());
        if (mSelectedImage != null){
            outState.putParcelable(getString(R.string.room_image_text), mSelectedImage);
        }
        super.onSaveInstanceState(outState);
    }


}
