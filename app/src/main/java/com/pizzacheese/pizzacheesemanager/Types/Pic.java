package com.pizzacheese.pizzacheesemanager.Types;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Tal on 21/08/2017.
 */

public class Pic {
    private String id;
    private Bitmap bm;
    private String path;

    public Pic(String id,Bitmap bm){
        this.bm=bm;
        this.id=id;

    }

    public Pic(String id){
        this.id=id;
    }


    public Pic(String id,String path){
        this.path=path;
        this.id=id;
        this.bm = BitmapFactory.decodeFile(path);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getBm() {
        return bm;
    }

    public void setBm(Bitmap bm) {
        this.bm = bm;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public void saveToInternalStorage(Context context){//saves a bitmap to internal storage and sets the pic's path to it

        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,this.id+".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.path= directory.getAbsolutePath();
    }


}
