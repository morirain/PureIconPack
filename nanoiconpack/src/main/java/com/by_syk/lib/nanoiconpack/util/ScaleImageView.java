package com.by_syk.lib.nanoiconpack.util;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.by_syk.lib.nanoiconpack.R;
import com.by_syk.lib.nanoiconpack.tasks.SetWallpaperTask;
import com.by_syk.lib.nanoiconpack.tasks.WallpaperApplyTask;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by morirain on 2018/4/29.
 * E-Mail Address：morirain.dev@outlook.com
 */


public class ScaleImageView {

    private static final byte URLS = 0;//网络查看状态
    private static final byte FILES = 1;//本地查看状态
    private byte mStatus;//用来表示当前大图查看器的状态

    private Activity mActivity;

    private WallpaperDownLoader mWallpaperDownLoader;

    private List<String> mUrls = new ArrayList<>();//网络查看状态中传入的要查看的图片的Url的List
    private List<File> mFiles = new ArrayList<>();//本地查看状态中传入的要查看的图片对应的file对象的List
    private HashMap<String, File> mDownloadFiles = new HashMap<String, File>();//网络查看状态中从Url下载下来的图片对应的Url的List

    private int selectedPosition;//表示当前被选中的ViewPager的item的位置

    private Dialog mDialog;//用于承载整个大图查看器的Dialog

    private ImageButton mSetWallpaper;//删除图片的按钮
    private ImageButton mDownload;//保存图片到本地的按钮
    private TextView tvImageCount;//用于显示当前正在查看第几张图片的TextView
    private ViewPager mViewPager;

    private List<View> mViews = new ArrayList<>();//ViewPager适配器的数据源
    private WallpaperPagerAdapter adapter;

    private OnDeleteItemListener listener;
    private int mStartPosition;//打开大图查看器时，想要查看的ViewPager的item的位置

    public ScaleImageView(Activity activity) {
        mActivity = activity;
        mWallpaperDownLoader = new WallpaperDownLoader();
        init();
    }

    public void setUrls(List<String> urls, int startPosition) {
        mUrls.clear();
        mUrls.addAll(urls);
        mStatus = URLS;
        //imDelete.setVisibility(View.GONE);
        mDownloadFiles.clear();
        mStartPosition = startPosition++;
        String text = startPosition + " / " + urls.size();
        tvImageCount.setText(text);
    }

    public void setFiles(List<File> files, int startPosition) {
        if (mFiles == null) {
            mFiles = new LinkedList<>();
        } else {
            mFiles.clear();
        }
        mFiles.addAll(files);
        mStatus = FILES;
        //imDownload.setVisibility(View.GONE);
        mStartPosition = startPosition++;
        String text = startPosition + " / " + files.size();
        tvImageCount.setText(text);
    }

    /*public void setOnDeleteItemListener(OnDeleteItemListener listener) {
        this.listener = listener;
    }*/

    private void init() {
        RelativeLayout relativeLayout = (RelativeLayout) mActivity.getLayoutInflater().inflate(R.layout.dialog_wallpaper, null);
        ImageButton close = (ImageButton) relativeLayout.findViewById(R.id.scale_image_close);
        mSetWallpaper = (ImageButton) relativeLayout.findViewById(R.id.scale_image_set_wallpaper);
        mDownload = (ImageButton) relativeLayout.findViewById(R.id.scale_image_save);
        tvImageCount = (TextView) relativeLayout.findViewById(R.id.scale_image_count);
        mViewPager = (ViewPager) relativeLayout.findViewById(R.id.scale_image_view_pager);

        mDialog = new Dialog(mActivity, R.style.WallpaperFullscreen);
        Objects.requireNonNull(mDialog.getWindow()).setWindowAnimations(R.style.dialogWindowAnim);
        mDialog.setContentView(relativeLayout);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        //设置壁纸
        mSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = mDownloadFiles.get(String.valueOf(selectedPosition));
                if (file == null) { return; }

                /*List<String> lists = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    lists.add(new PopupItem(context.getResources().getString(R.string.menu_apply_lockscreen))
                            .setType(WallpaperApplyTask.ApplyType.LOCKSCREEN)
                            .setIcon(R.drawable.ic_toolbar_lockscreen));
                }

                lists.add(new PopupItem(context.getResources().getString(R.string.menu_apply_homescreen))
                        .setType(WallpaperApplyTask.ApplyType.HOMESCREEN)
                        .setIcon(R.drawable.ic_toolbar_homescreen));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    lists.add(new PopupItem(context.getResources().getString(R.string.menu_apply_homescreen_lockscreen))
                            .setType(WallpaperApplyTask.ApplyType.HOMESCREEN_LOCKSCREEN)
                            .setIcon(R.drawable.ic_toolbar_homescreen_lockscreen));
                }

                if (context.getResources().getBoolean(R.bool.enable_wallpaper_download)) {
                    lists.add(new PopupItem(context.getResources().getString(R.string.menu_save))
                            .setType(WallpaperApplyTask.ApplyType.DOWNLOAD)
                            .setIcon(R.drawable.ic_toolbar_download));
                }
                ListPopupWindow listPopupWindow = new ListPopupWindow(mActivity);
                listPopupWindow = new ListPopupWindow(mActivity);
                listPopupWindow.setAdapter(new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, lists));
                listPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                listPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                listPopupWindow.setAnchorView(mSetWallpaper);//设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
                listPopupWindow.setModal(true);//设置是否是模式
                listPopupWindow.show();*/
                SetWallpaperTask.prepare(mActivity)
                        .wallpaper(file)
                        .callback(new SetWallpaperTask.SetWallpaperListener() {
                            @Override
                            public void onApplyCompleted() {
                                Snackbar.make(mViewPager, R.string.snackbar_wallpaper_apply, Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Snackbar.make(mViewPager, R.string.snackbar_wallpaper_apply_failure, Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .start(AsyncTask.THREAD_POOL_EXECUTOR);




            }
        });

        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = mDownloadFiles.get(String.valueOf(selectedPosition));
                if (file == null) { return; }
                /* GetPermission */
                if (C.SDK >= 23 && mActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    mActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }


                String oldPath = file.getAbsolutePath();
                String newPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" +
                        mDownloadFiles.get(String.valueOf(selectedPosition)).getName() + ".jpg";
                LogUtil.d(String.valueOf(mDownloadFiles));
                mWallpaperDownLoader.copyFile(oldPath, newPath, new WallpaperDownLoader.CopyCallback() {
                    @Override
                    public void onCopy() {
                        LogUtil.d("onCopy");
                        MediaScannerConnection.scanFile(mActivity,
                                arrayOf(newPath),
                                arrayOf("image/jpeg"),
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        Snackbar.make(mViewPager, R.string.snackbar_wallpaper_download, Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure() {
                        LogUtil.d("onFailure");
                        Snackbar.make(mViewPager, R.string.snackbar_wallpaper_download_failure, Snackbar.LENGTH_SHORT).show();
                    }
                });


            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedPosition = position;
                String text = ++position + " / " + mViews.size();
                tvImageCount.setText(text);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private String[] arrayOf(String string) {
        return new String[]{string};
    }

    public void create() {
        mDialog.show();
        mViews = new ArrayList<>();
        adapter = new WallpaperPagerAdapter(mViews, mDialog);
        if (mStatus == URLS) {
            int num = -1;
            for (String url : mUrls) {
                num += 1;
                FrameLayout frameLayout = (FrameLayout) mActivity.getLayoutInflater().inflate(R.layout.item_wallpaper_pager, null);
                SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) frameLayout.findViewById(R.id.scale_image_view);
                PhotoView wallpaperView = frameLayout.findViewById(R.id.scale_wallpaper_view);
                wallpaperView.setOnPhotoTapListener(new OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(ImageView view, float x, float y) {
                        mDialog.dismiss();
                    }
                });
                mViews.add(frameLayout);
                //这个变量用于分辨顺序
                int finalNum = num;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mWallpaperDownLoader.downLoad(url, mActivity, new WallpaperDownLoader.DownCallback() {
                            @Override
                            public void onDownload(File file) {

                                LogUtil.d("onDownload " + file);
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 调整次序 以保证顺序
                                        mDownloadFiles.put(String.valueOf(finalNum), file);

                                        // 把图片显示在画面中
                                        Glide.with(mActivity).load(file).into(wallpaperView);//imageView.setImage(ImageSource.uri(Uri.fromFile(downLoadFile)));
                                    }
                                });
                            }

                            @Override
                            public void onFailure() {

                                LogUtil.d("onFailure ");
                            }
                        });

                    }
                }).start();

                //IOThread.getSingleThread().execute(() -> {

                //});
            }
            mViewPager.setAdapter(adapter);
        } else if (mStatus == FILES) {
            for (File file : mFiles) {
                FrameLayout frameLayout = (FrameLayout) mActivity.getLayoutInflater().inflate(R.layout.item_wallpaper_pager, null);
                SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) frameLayout.findViewById(R.id.scale_image_view);
                PhotoView wallpaperView = frameLayout.findViewById(R.id.scale_wallpaper_view);
                mViews.add(frameLayout);
                Glide.with(mActivity).load(file).into(wallpaperView);//imageView.setImage(ImageSource.uri(Uri.fromFile(file)));
            }
            mViewPager.setAdapter(adapter);
        }
        mViewPager.setCurrentItem(mStartPosition);
    }

    private static class WallpaperPagerAdapter extends PagerAdapter {

        private List<View> views;
        private Dialog dialog;

        WallpaperPagerAdapter(List<View> views, Dialog dialog) {
            this.views = views;
            this.dialog = dialog;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position == 0 && views.size() == 0) {
                dialog.dismiss();
                return;
            }
            if (position == views.size()) {
                container.removeView(views.get(--position));
            } else {
                container.removeView(views.get(position));
            }
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    public interface OnDeleteItemListener {
        void onDelete(int position);
    }

}

