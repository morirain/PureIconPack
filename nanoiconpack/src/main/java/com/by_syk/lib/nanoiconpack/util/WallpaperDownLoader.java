package com.by_syk.lib.nanoiconpack.util;


import android.app.Activity;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class WallpaperDownLoader {


    public void downLoad(String url, Activity activity, @NonNull DownCallback callback) {
        File file = null;
        try {
            file = Glide.with(activity).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
            callback.onDownload(file);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            callback.onFailure();
        }
    }

    public void copyFile(String oldPath, String newPath, @NonNull CopyCallback copyCallback) {

        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
            copyCallback.onCopy();
        } catch (IOException e) {
            copyCallback.onFailure();
            e.printStackTrace();
        }



    }

    public interface DownCallback {
        void onDownload(File file);
        void onFailure();
    }

    public interface CopyCallback {
        void onCopy();
        void onFailure();

    }
}
