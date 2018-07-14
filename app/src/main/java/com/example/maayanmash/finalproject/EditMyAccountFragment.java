package com.example.maayanmash.finalproject;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.maayanmash.finalproject.Model.Model;
import com.example.maayanmash.finalproject.Model.ModelFirebase;
import com.example.maayanmash.finalproject.Model.entities.User;

import static android.app.Activity.RESULT_OK;

public class EditMyAccountFragment extends Fragment {
    private EditText name;
    private EditText email;
    private EditText phone;
    private EditText address;
    private ImageView img;
    private User _user;
    private View progressBar;
    private Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    final static int RESAULT_SUCCESS = 0;

    public EditMyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_my_account, container, false);
        name = view.findViewById(R.id.EditMyAccount_name_et);
        email = view.findViewById(R.id.EditMyAccount_email_et);
        phone = view.findViewById(R.id.EditMyAccount_phone_et);
        address = view.findViewById(R.id.EditMyAccount_address_et);
        img= view.findViewById(R.id.EditMyAccount_avatar_img);
        progressBar= view.findViewById(R.id.EditMyAccount_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        Model.instance.getMyUserDetails(Model.instance.getuID(), new MainActivity.GetUserDetailsCallback() {

            @Override
            public void onComplete(User user) {
                Log.d("TAG", user.toString());
                _user=user;
                if (user.getImage()!=null){
                    Model.instance.getImage(_user.getImage(), new Model.GetImageListener() {
                        @Override
                        public void onDone(Bitmap imageBitmap) {
                            img.setImageBitmap(imageBitmap);
                        }
                    });
                }
                name.setText(user.getName());
                email.setText(user.getEmail());
                phone.setText(user.getPhone());
                address.setText(user.getAddress());

            }

            @Override
            public void onFailure() {

            }
        });

        Button save=view.findViewById(R.id.EditMyAccount_save_btn);
        Button cancel=view.findViewById(R.id.EditMyAccount_cancel_btn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               takeAPicture();
            }
        });
        return view;
    }

    private void takeAPicture(){
        //open camera
        Intent takePictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            img.setImageBitmap(imageBitmap);
        }
    }

    public void saveDetails(){
        progressBar.setVisibility(View.VISIBLE);
        _user.setName(name.getText().toString());
        _user.setEmail(email.getText().toString());
        _user.setPhone(phone.getText().toString());
        _user.setAddress(address.getText().toString());

        //save image
        if (imageBitmap != null) {
            Model.instance.saveImage(imageBitmap, _user.getUid(), new Model.SaveImageListener() {
                @Override
                public void onComplete(String url) {
                    _user.setImage(url);
                    saveUser();
                }

                @Override
                public void fail() {saveUser();}
            });
        }
        else {
            saveUser();
        }
    }

    private void saveUser(){
        Model.instance.updateUserDetails(_user);
        progressBar.setVisibility(View.INVISIBLE);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onDetach() {
         super.onDetach();
    }

}
