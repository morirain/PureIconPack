package com.by_syk.lib.nanoiconpack.util.tasks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;

import com.by_syk.lib.nanoiconpack.bean.AppBean;
import com.by_syk.lib.nanoiconpack.util.C;
import com.by_syk.lib.nanoiconpack.util.ExtraUtil;
import com.by_syk.lib.nanoiconpack.util.FileUtils;
import com.by_syk.lib.nanoiconpack.util.PkgUtil;
import com.by_syk.lib.nanoiconpack.util.ZipUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



/**
 * Created by morirain on 2018/5/5.
 * E-Mail Address：morirain.dev@outlook.com
 */


public class RequestIconTask extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<Context> mContext;
    private final List<AppBean> mDataLists;
    private final File mDirCache;
    private File mFileIconCode;
    private File mOutZip;
    private final List<File> mFileIcon = new ArrayList<>();
    private final File mExternalCacheDir;

    public RequestIconTask(Context context, List<AppBean> dataLists) {
        this.mContext = new WeakReference<>(context);
        this.mDataLists = dataLists;
        this.mDirCache = new File(mContext.get().getCacheDir() + "/icon_request");
        this.mExternalCacheDir = mContext.get().getExternalCacheDir();
        if (FileUtils.createOrExistsDir(mDirCache)) {
            this.mFileIconCode = new File(mDirCache + "/IconCode.txt");
            /* 先删除之前留下的文件 */
            FileUtils.deleteAllInDir(mDirCache);
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        //File[] files = new File[mDataLists.size()];

        for (int i = 0; i < mDataLists.size(); i++) {
            if (copyAppCode(mDataLists.get(i))) return false;
            if (copyAppIcon(mDataLists.get(i))) return false;
        }

        /* 设置压缩包内的文件 */
        List<File> zipFiles = new ArrayList<>(mFileIcon);
        zipFiles.add(mFileIconCode);
        /* 设置压缩文件的名称 */
        this.mOutZip = new File(mExternalCacheDir + "/IconRequest_" + zipFiles.size() + ".zip");
        try {
            /* 压缩并判断是否成功 */        /* //压缩完删除原文件 */
            if (ZipUtils.zipFiles(zipFiles, mOutZip)) return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean && mContext.get() != null) {
            Uri uri = Uri.parse("mailto:pure-iconpack@outlook.com");
            Intent email = new Intent(Intent.ACTION_SEND, uri);
            //邮件发送类型：带附件的邮件
            email.setType("application/octet-stream");
            //邮件接收者（数组，可以是多位接收者）
            String[] emailReceiver = {"pure-iconpack@outlook.com"};
            String emailTitle = "Pure: icon request";
            String emailContent = "";
            //设置邮件地址
            email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReceiver);
            //email.putExtra(Intent.EXTRA_CC, emailReceiver); // 抄送人
            //设置邮件标题
            email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailTitle);
            //设置发送的内容
            email.putExtra(android.content.Intent.EXTRA_TEXT, emailContent);


            Uri outFileUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                outFileUri = FileProvider.getUriForFile(mContext.get(), "com.by_syk.lib.nanoiconpack.fileprovider", this.mOutZip);
                email.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                outFileUri = Uri.fromFile(this.mOutZip);
            }
            //附件
            email.putExtra(Intent.EXTRA_STREAM,outFileUri);
            //调用系统的邮件系统
            mContext.get().startActivity(Intent.createChooser(email, "Email to"));
        } else if (!aBoolean && mContext != null){
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext.get());
            dialog.setTitle("Request icon");
            dialog.setMessage("Error!");
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", null);
            dialog.show();
        }
    }

    /**
     * Drawable to bitmap.
     *
     * @param drawable The drawable.
     * @return bitmap
     */
    private static Bitmap drawable2Bitmap(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /* Complete */
    private boolean copyAppIcon(AppBean bean) {

        Drawable drawable = bean.getIcon();
        String name = bean.getLabel();

        Bitmap bitmap = null;
        if (drawable != null) bitmap = drawable2Bitmap(drawable);//((BitmapDrawable) drawable).getBitmap();
        if (bitmap == null) return false;
        // Make sure the Pictures directory exists.
        File targetFile = new File(mDirCache, "ic_" + name + "_"
                + bitmap.getByteCount() + ".png");

        boolean result = false;
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(targetFile);
            result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!result) {
            //FileUtils.deleteFile(targetFile);
            return false;
        }

        mFileIcon.add(targetFile);
        return true;

    }

    /* Complete */
    private boolean copyAppCode(AppBean bean) {
        if (bean == null || bean.getPkg().equals(bean.getLauncher())) {
            //GlobalToast.show(mContext, R.string.toast_code_copy_failed);
            return false;
        }

        String label = bean.getLabel();
        String labelEn = PkgUtil.getAppLabelEn(mContext.get(), bean.getPkg(), null);
        String iconName = ExtraUtil.codeAppName(labelEn);
        if (iconName.isEmpty()) {
            iconName = ExtraUtil.codeAppName(label);
        }
        //boolean isSysApp = PkgUtil.isSysApp(mContext.get(), bean.getPkg());
        String code = String.format(Locale.US, C.APP_CODE_LABEL, label, labelEn);
        code += "\n" + String.format(Locale.US, C.APP_CODE_COMPONENT,
                bean.getPkg(), bean.getLauncher(), iconName);
        /*if (isSysApp) {
            code = String.format(Locale.US, C.APP_CODE_BUILD, Build.BRAND, Build.MODEL) + "\n" + code;
        }*/
        code += "\n\n";

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(mFileIconCode, true)));
            out.write(code);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;

    }



}
