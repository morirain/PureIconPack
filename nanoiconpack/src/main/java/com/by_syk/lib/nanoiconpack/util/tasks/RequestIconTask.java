package com.by_syk.lib.nanoiconpack.util.tasks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by morirain on 2018/5/5.
 * E-Mail Address：morirain.dev@outlook.com
 */


public class RequestIconTask extends AsyncTask<Void, Void, Boolean> {
    private Context mContext;
    private List<AppBean> mDataLists;
    private File mDirCache;
    private File mFileIconCode;
    private File mOutZip;
    private List<File> mFileIcon = new ArrayList<>();

    public RequestIconTask(Context context, List<AppBean> dataLists) {
        this.mContext = context;
        this.mDataLists = dataLists;
        mDirCache = new File(mContext.getCacheDir() + "/icon_request");
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
            copyAppCode(mDataLists.get(i));
            copyAppIcon(mDataLists.get(i));
        }

        /* 设置压缩包内的文件 */
        List<File> zipFiles = new ArrayList<>(mFileIcon);
        zipFiles.add(mFileIconCode);
        /* 设置压缩文件的名称 */
        this.mOutZip = new File(mContext.getExternalCacheDir() + "/IconRequest_" + zipFiles.size() + ".zip");
        try {
            /* 压缩并判断是否成功 */                    /* 压缩完删除原文件 */
            ZipUtils.zipFiles(zipFiles, mOutZip);

            {
                Uri uri = Uri.parse("mailto:morirain.dev@outlook.com");
                Intent email = new Intent(Intent.ACTION_SEND, uri);
                //邮件发送类型：带附件的邮件
                email.setType("application/octet-stream");
                //邮件接收者（数组，可以是多位接收者）
                String[] emailReceiver = {"morirain.dev@outlook.com"};
                String emailTitle = "Pure: icon request";
                String emailContent = "";
                //设置邮件地址
                email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReceiver);
                //email.putExtra(Intent.EXTRA_CC, emailReceiver); // 抄送人
                //设置邮件标题
                email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailTitle);
                //设置发送的内容
                email.putExtra(android.content.Intent.EXTRA_TEXT, emailContent);
                //附件
                email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mOutZip));
                //调用系统的邮件系统
                mContext.startActivity(Intent.createChooser(email, "Email to"));
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    /* Complete */
    private boolean copyAppIcon(AppBean bean) {

        Drawable drawable = bean.getIcon();
        String name = bean.getLabel();

        Bitmap bitmap = null;
        if (drawable != null) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        if (bitmap == null) {
            return false;
        }

        // Create a path where we will place our picture
        // in the user's public pictures directory.

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
            FileUtils.deleteFile(targetFile);
            return false;
        }

        mFileIcon.add(targetFile);
        return true;

    }

    /* Complete */
    private void copyAppCode(AppBean bean) {
        if (bean == null || bean.getPkg().equals(bean.getLauncher())) {
            //GlobalToast.show(mContext, R.string.toast_code_copy_failed);
            return;
        }

        String label = bean.getLabel();
        String labelEn = PkgUtil.getAppLabelEn(mContext, bean.getPkg(), null);
        String iconName = ExtraUtil.codeAppName(labelEn);
        if (iconName.isEmpty()) {
            iconName = ExtraUtil.codeAppName(label);
        }
        boolean isSysApp = PkgUtil.isSysApp(mContext, bean.getPkg());
        String code = String.format(Locale.US, C.APP_CODE_LABEL, label, labelEn);
        code += "\n" + String.format(Locale.US, C.APP_CODE_COMPONENT,
                bean.getPkg(), bean.getLauncher(), iconName);
        if (isSysApp) {
            code = String.format(Locale.US, C.APP_CODE_BUILD, Build.BRAND, Build.MODEL) + "\n" + code;
        }
        code += "\n\n";

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(mFileIconCode, true)));
            out.write(code);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }



}
