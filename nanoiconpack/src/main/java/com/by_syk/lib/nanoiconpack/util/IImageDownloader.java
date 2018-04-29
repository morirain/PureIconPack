package com.by_syk.lib.nanoiconpack.util;


import android.app.Activity;

import java.io.File;

public interface IImageDownloader {
    File downLoad(String url, Activity activity);
}
