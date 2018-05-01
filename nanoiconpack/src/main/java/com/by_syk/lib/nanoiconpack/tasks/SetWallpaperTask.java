package com.by_syk.lib.nanoiconpack.tasks;


import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.by_syk.lib.nanoiconpack.R;
import com.by_syk.lib.nanoiconpack.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

/**
 * Created by morirain on 2018/4/30.
 * E-Mail Address：morirain.dev@outlook.com
 */


public class SetWallpaperTask extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<Activity> mContext;
    private ProgressDialog mDialog;
    private Executor mExecutor;
    private File mWallpaper;
    private SetWallpaperListener mSetWallpaperListener;

    private SetWallpaperTask(Activity context)  {
        mContext = new WeakReference<>(context);
    }

    public static SetWallpaperTask prepare(@NonNull Activity context) {
        return new SetWallpaperTask(context);
    }

    public SetWallpaperTask wallpaper(@NonNull File wallpaper) {
        mWallpaper = wallpaper;
        return this;
    }

    public SetWallpaperTask callback(SetWallpaperListener setWallpaperListener) {
        this.mSetWallpaperListener = setWallpaperListener;
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
            mDialog = new ProgressDialog(mContext.get());
            mDialog.setTitle(R.string.dlg_title_changing_wallpaper);
            mDialog.setCancelable(false);
        }
        if (!mDialog.isShowing() && !mContext.get().isFinishing()) mDialog.show();
        mExecutor = executor;
        return executeOnExecutor(executor);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        // Callback
        if (mSetWallpaperListener != null) {
            if (aBoolean) {
                mSetWallpaperListener.onApplyCompleted();
            } else {
                mSetWallpaperListener.onFailure();
            }
        }
        mDialog.dismiss();
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
        // 获取屏幕长宽
        WindowManager manager = mContext.get().getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        // 姑且优化
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 2;
        // 载入
        Bitmap bmp = BitmapFactory.decodeFile(mWallpaper.getAbsolutePath());//, options);
        // 若图片大于屏幕 则进行缩放
        if (bmp.getHeight() > height) {
            bmp = ImageCrop(bmp, width, height);//clip(bmp, width, height, bmp.getWidth());
        }
        // 更换壁纸
        try {
            WallpaperManager wallpaperManager = (WallpaperManager) mContext.get().getSystemService(
                    Context.WALLPAPER_SERVICE);
            if (bmp != null && wallpaperManager != null) {
                wallpaperManager.setBitmap(bmp);
            }
            LogUtil.d("onSetWallpaper");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Create by morirain.
     * 居中对齐图片*/
    private Bitmap ImageCrop(Bitmap bitmap, int sWidth, int sHeight)
    {
        if (bitmap == null)
        {
            return null;
        }

        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();


        float bitProp = getProportion(h, sHeight); //获取图片的比例 1.3左右
        float nw = (w / bitProp) / w; //width缩放后的比例
        float nh = getProportion(sHeight, h); //获取屏幕与图片的Y轴比例 0.6左右

        Matrix matrix = new Matrix();
        matrix.postScale(nw, nh);

        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        int retX = (bmp.getWidth() / 2) - (sWidth / 2);
        int retY = 0;
        return Bitmap.createBitmap(bmp, retX, retY, sWidth, sHeight, null, false);
    }

    private float getProportion(int a, int b) {
        return (float) a / (float) b;
    }

    public interface SetWallpaperListener {
        void onApplyCompleted();
        void onFailure();
    }

}

