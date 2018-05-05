package com.by_syk.lib.nanoiconpack.tasks;


/**
 * Created by morirain on 2018/4/30.
 * E-Mail Address：morirain.dev@outlook.com
 */
/*

public class WallpaperApplyTask extends AsyncTask<Void, Void, Boolean> {

    private Context mContext;
    private Executor mExecutor;
    private ApplyType mApplyType;
    private ProgressDialog mDialog;
    private WallpaperBean mWallpaper;

    public enum ApplyType {
        LOCKSCREEN,
        HOMESCREEN,
        BOTHSCREEN
    }

    private WallpaperApplyTask(Context context) {
        mContext = context;
    }

    public WallpaperApplyTask to(ApplyType apply) {
        mApplyType = apply;
        return this;
    }

    public WallpaperApplyTask wallpaper(@NonNull WallpaperBean wallpaper) {
        mWallpaper = wallpaper;
        return this;
    }

    public AsyncTask start() {
        return start(SERIAL_EXECUTOR);
    }

    public AsyncTask start(@NonNull Executor executor) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setTitle("正在更换壁纸");
            mDialog.setCancelable(false);
        }
        if (!mDialog.isShowing()) mDialog.show();

        mExecutor = executor;
        if (mWallpaper == null) {
            LogUtil.e("WallpaperApply cancelled, wallpaper is null");
            return null;
        }

        return executeOnExecutor(executor);
    }

    public static WallpaperApplyTask prepare(@NonNull Context context) {
        return new WallpaperApplyTask(context);
    }



    @Override
    protected Boolean doInBackground(Void... voids) {
        return null;
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
}
*/