package com.example.maayanmash.finalproject.Model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.maayanmash.finalproject.MainActivity;
import com.example.maayanmash.finalproject.Model.entities.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Model {
    public static Model instance = new Model();
    ModelFirebase modelFirebase;

    private Model() {
        modelFirebase = new ModelFirebase();
    }

    public User getMyUserDetails(final String uID, final MainActivity.GetUserDetailsCallback callback) {
        return modelFirebase.getMyUserDetails(uID, callback);
    }

    public String getuID() {
        return modelFirebase.getuID();
    }

    public void updateDestinationArrivalForTask(final String dID, final boolean isDone) {
        modelFirebase.updateDestinationArrivalForTask(dID, isDone);
    }

    public void updateMyLocation(Double latitude, Double longitude) {
        modelFirebase.updateMyLocation(latitude, longitude);
    }

    public void cleanData() {
        modelFirebase.cleanData();
    }

    public void getMyDestinationsByID(final String uID, final MainActivity.GetDestinationsForUserIDCallback callback) {
        modelFirebase.getMyDestinationsByID(uID, callback);
    }

    public void updateUserDetails(User user) {
        modelFirebase.updateUserDetails(user);
    }

     ///////////////////////////////////////////////////////
    //                Image Files                        //
   ///////////////////////////////////////////////////////

    public void saveImage(final Bitmap imageBmp, final String name, final SaveImageListener listener) {
        //1. save the image remotly
        modelFirebase.saveImage(imageBmp, name, new SaveImageListener() {
            @Override
            public void onComplete(String url) {
                // 2. saving the file localy
                String nameURL = URLUtil.guessFileName(url, null, null);
                saveImageToFile(imageBmp, nameURL); // synchronously save image locally
                listener.onComplete(url);
            }

            @Override
            public void fail() {
                listener.fail();
            }
        });
    }

    public interface SaveImageListener {
        void onComplete(String url);
        void fail();
    }

    private void saveImageToFile(Bitmap imageBitmap, String imageFileName) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir, imageFileName);
            imageFile.createNewFile();
            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            //addPicureToGallery(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface GetImageListener{
        void onDone(Bitmap imageBitmap);
    }
    public void getImage(final String url, final GetImageListener listener ){
        String localFileName = URLUtil.guessFileName(url, null, null);
        final Bitmap image = loadImageFromFile(localFileName);
        if (image == null) {                                      //if image not found - try downloading it from parse
            modelFirebase.getImage(url, new GetImageListener() {
                @Override
                public void onDone(Bitmap imageBitmap) {
                    if (imageBitmap == null) {
                        listener.onDone(null);
                    }else {
                        //2.  save the image localy
                        String localFileName = URLUtil.guessFileName(url, null, null);
                        Log.d("TAG", "save image to cache: " + localFileName);
                        saveImageToFile(imageBitmap, localFileName);
                        //3. return the image using the listener
                        listener.onDone(imageBitmap);
                    }
                }
            });
        }else {
            listener.onDone(image);
        }
    }

    private Bitmap loadImageFromFile(String imageFileName){
        Bitmap bitmap = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir,imageFileName);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}



