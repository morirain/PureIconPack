package com.by_syk.lib.nanoiconpack.util;


import android.app.Activity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;

public class WallpaperDownLoader implements IImageDownloader {
    @Override
    public File downLoad(String url, Activity activity) {
        File file = null;
        try {
            file = Glide.with(activity).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
