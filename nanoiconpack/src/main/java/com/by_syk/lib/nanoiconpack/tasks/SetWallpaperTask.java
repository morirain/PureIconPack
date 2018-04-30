package com.by_syk.lib.nanoiconpack.tasks;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.by_syk.lib.nanoiconpack.bean.WallpaperBean;
import com.by_syk.lib.nanoiconpack.util.LogUtil;

import java.io.File;
import java.util.concurrent.Executor;

/**
 * Created by morirain on 2018/4/30.
 * E-Mail Address：morirain.dev@outlook.com
 */


public class SetWallpaperTask extends AsyncTask<Void, Void, Boolean> {
    private Context mContext;
    private ProgressDialog mDialog;
    private Executor mExecutor;
    private File mWallpaper;

    private SetWallpaperTask(Context context) {
        mContext = context;
    }

    public static SetWallpaperTask prepare(@NonNull Context context) {
        return new SetWallpaperTask(context);
    }

    public SetWallpaperTask wallpaper(@NonNull File wallpaper) {
        mWallpaper = wallpaper;
        return this;
    }

    public AsyncTask start() {
        return start(SERIAL_EXECUTOR);
    }

    public AsyncTask start(@NonNull Executor executor) {
        if (mWallpaper == null) {
            LogUtil.e("WallpaperApply cancelled, wallpaper is null");
            return null;
        }
        if (mDialog == null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setTitle("正在更换壁纸");
            mDialog.setCancelable(false);
        }
        if (!mDialog.isShowing()) mDialog.show();

        return executeOnExecutor(executor);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        super.onCancelled(aBoolean);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return null;
    }
}
