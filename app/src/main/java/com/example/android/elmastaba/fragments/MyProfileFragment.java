package com.example.android.elmastaba.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.elmastaba.MainActivity;
import com.example.android.elmastaba.R;
import com.example.android.elmastaba.Service.StorageFirebaseService;
import com.example.android.elmastaba.SignInActivity;
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

import static android.app.Activity.RESULT_OK;
import static android.os.Build.VERSION_CODES.O;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Context mContext;

    private EditText mUsername;
    private EditText mAddress;
    private EditText mBirthDay;
    private EditText mMobile;
    private ImageView mUserImage;
    private Button mSaveButton;

    private FirebaseUser mFirebaseUser;
    private DatabaseReference mUserDatabaseRef;

    private Uri mSelectedImage;

    private User mUser;

    private View rootView;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        mContext = (Context) getContext();
        //Set title of the fragment.
        getActivity().setTitle(getString(R.string.nav_my_profile));

        mUsername = rootView.findViewById(R.id.my_profile_name_edit_text);
        mAddress = rootView.findViewById(R.id.my_profile_address_edit_text);
        mBirthDay = rootView.findViewById(R.id.my_profile_birthday_date_edit_text);
        mMobile = rootView.findViewById(R.id.my_profile_mobile_edit_text);
        mUserImage = rootView.findViewById(R.id.my_profile_image);
        mSaveButton = rootView.findViewById(R.id.my_profile_save_button);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.Users_text)).child(mFirebaseUser.getUid());

            mUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mUser = dataSnapshot.getValue(User.class);
                    //setting the user data to views.
                    setUserDataToViews(mUser.getmName(), mUser.getmAddress(), mUser.getmBirthDay(),
                            mUser.getmMobileNum(),mUser.getmPhotoUrl());
                    if (savedInstanceState != null){
                        if (savedInstanceState.containsKey(getResources().getString(R.string.user_name_text))){
                            mUsername.setText(savedInstanceState.getString(getResources().getString(R.string.user_name_text)));
                        }
                        if (savedInstanceState.containsKey(getResources().getString(R.string.user_address_text))){
                            mAddress.setText(savedInstanceState.getString(getResources().getString(R.string.user_address_text)));

                        }
                        if (savedInstanceState.containsKey(getResources().getString(R.string.user_birthday_text))){
                            mBirthDay.setText(savedInstanceState.getString(getResources().getString(R.string.user_birthday_text)));
                        }
                        if (savedInstanceState.containsKey(getResources().getString(R.string.user_mobile_text))){
                            mMobile.setText(savedInstanceState.getString(getResources().getString(R.string.user_mobile_text)));
                        }
                        if (savedInstanceState.containsKey(getResources().getString(R.string.user_image_text))){
                            Picasso.with(mContext)
                                    .load(Uri.parse(savedInstanceState.getString(getResources().getString(R.string.user_image_text))))
                                    .resize(360,360)
                                    .into(mUserImage);

                        }
                    }
                    setPickImageRequest();
                    setSaveButtonListener();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        return rootView;
    }

    private void setUserDataToViews(String name, String address, String birthday,String mobileNum, String photoUrl){
        mUsername.setHint(mUser.getmName());
        if (!TextUtils.isEmpty(mUser.getmAddress())){
            mAddress.setHint(mUser.getmAddress());
        }
        if (! TextUtils.isEmpty(mUser.getmBirthDay())){
            mBirthDay.setHint(mUser.getmBirthDay());
        }
        if (! TextUtils.isEmpty(mUser.getmMobileNum())){
            mMobile.setHint(mUser.getmMobileNum());
        }
        if (! TextUtils.isEmpty(mUser.getmPhotoUrl())){
            Picasso.with(mContext)
                    .load(mUser.getmPhotoUrl())
                    .resize(360,360)
                    .into(mUserImage);
        }

    }

    private void setPickImageRequest(){
        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType(getResources().getString(R.string.all_image_types));
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture_text)), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void setSaveButtonListener(){
        if (! SignInActivity.isNetworkAvailable(mContext)){
            Toast.makeText(mContext,
                    getString(R.string.internet_connection_unavailable_string), Toast.LENGTH_LONG).show();
            return;
        }
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mUsername.getText().toString().trim();
                String address = mAddress.getText().toString().trim();
                String birthDay = mBirthDay.getText().toString().trim();
                String mobile = mMobile.getText().toString().trim();

                boolean validData = true;
                //Validate name .
                if (userName.isEmpty()) {
                    userName = mUser.getmName();
                } else if (userName.length() < 4) {
                    mUsername.setError(getResources().getString(R.string.username_validation_error_message));
                    validData = false;
                }else {
                    for (char ch : userName.toCharArray()) {
                        if (!Character.isDigit(ch) && !Character.isLetter(ch) && ch == ' ' && ch !='_') {
                            mUsername.setError(getResources().getString(R.string.username_validation_characters_error_message));
                            validData = false;
                        }
                    }
                }

                //Validate address
                if (address.isEmpty()){
                    address = mUser.getmAddress();
                }

                //Validate Birthday
                if (birthDay.isEmpty()){
                    birthDay = mUser.getmBirthDay();
                }

                //Validate mobile
                if (mobile.isEmpty()){
                   mobile = mUser.getmMobileNum();
                }

                //Start service to add update the used data on firebase database.
                Intent intent = new Intent(mContext, StorageFirebaseService.class);
                intent.setAction(getResources().getString(R.string.action_update_profile));
                intent.putExtra(getResources().getString(R.string.user_name_extra_text),userName);
                intent.putExtra(getResources().getString(R.string.address_extra_text),address);
                intent.putExtra(getResources().getString(R.string.birth_day_extra_text),birthDay);
                intent.putExtra(getResources().getString(R.string.mobile_extra_text),mobile);
                intent.putExtra(getResources().getString(R.string.photo_extra_text),mSelectedImage);
                mContext.startService(intent);
                //Show toast of success.
                Toast.makeText(getContext(), getResources().getString(R.string.my_profile_edit_success), Toast.LENGTH_LONG).show();
                //Move to Home fragment.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, new HomeFragment());
                ft.commit();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mSelectedImage = data.getData();

            Picasso.with(mContext)
                    .load(mSelectedImage)
                    .resize(360,360)
                    .into(mUserImage);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //TODO : put the editText and image data in here.
        String userName = mUsername.getText().toString().trim();
        String address = mAddress.getText().toString().trim();
        String birthDay = mBirthDay.getText().toString().trim();
        String mobile = mMobile.getText().toString().trim();

        if (userName != null){
            outState.putString(getResources().getString(R.string.user_name_text), userName);
        }
        if (! address.isEmpty()){
            outState.putString(getResources().getString(R.string.user_address_text), address);
        }

        //Validate Birthday
        if (! birthDay.isEmpty()){
            outState.putString(getResources().getString(R.string.user_birthday_text), birthDay);
        }

        //Validate mobile
        if (! mobile.isEmpty()){
            outState.putString(getResources().getString(R.string.user_mobile_text), mobile);
        }

        if (! Uri.EMPTY.equals(mSelectedImage) && mSelectedImage != null ){
            outState.putString(getResources().getString(R.string.user_image_text), mSelectedImage.toString());
        }

        super.onSaveInstanceState(outState);
    }

}
