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
        File file;
        try {
            file = Glide.with(activity).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
            callback.onDownload(file);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            callback.onFailure();
        }
    }

    public void copyFile(String oldPath, String newPath, @NonNull CopyCallback copyCallback) {
        int byteSum = 0;
        int byteRead;
        InputStream inStream = null;
        FileOutputStream fs = null;
        File oldFile = new File(oldPath);
        try {
            if (oldFile.exists()) { //文件存在时
                inStream = new FileInputStream(oldPath); //读入原文件
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ( (byteRead = inStream.read(buffer)) != -1) {
                    byteSum += byteRead; //字节数 文件大小
                    System.out.println(byteSum);
                    fs.write(buffer, 0, byteRead);
                }
            }
            copyCallback.onCopy();
        } catch (IOException e) {
            copyCallback.onFailure();
            e.printStackTrace();
        } finally {
        try {
            if (inStream != null) {
                inStream.close();
            }
            if (fs != null) {
                fs.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
