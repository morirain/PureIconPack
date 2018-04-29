package com.by_syk.lib.nanoiconpack.util;


import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.by_syk.lib.nanoiconpack.R;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.SET_WALLPAPER;
import static android.content.ContentValues.TAG;

/**
 * Created by morirain on 2018/4/29.
 * E-Mail Address：morirain.dev@outlook.com
 */


public class ScaleImageView {

    private static final byte URLS = 0;//网络查看状态
    private static final byte FILES = 1;//本地查看状态
    private byte mStatus;//用来表示当前大图查看器的状态

    private Activity mActivity;

    private IImageDownloader mIImageDownloader;

    private List<String> mUrls = new ArrayList<>();//网络查看状态中传入的要查看的图片的Url的List
    private List<File> mFiles = new ArrayList<>();//本地查看状态中传入的要查看的图片对应的file对象的List
    private List<File> mDownloadFiles = new ArrayList<>();//网络查看状态中从Url下载下来的图片对应的Url的List

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

    public ScaleImageView(Activity activity, IImageDownloader IImageDownloader) {
        mActivity = activity;
        mIImageDownloader = IImageDownloader;
        init();
    }

    public void setUrls(List<String> urls, int startPosition) {
        if (mUrls == null) {
            mUrls= new ArrayList<>();
        } else {
            mUrls.clear();
        }
        mUrls.addAll(urls);
        mStatus = URLS;
        //imDelete.setVisibility(View.GONE);
        if (mDownloadFiles == null) {
            mDownloadFiles = new ArrayList<>();
        } else {
            mDownloadFiles.clear();
        }
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
                //File picFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" +
                //        mDownloadFiles.get(selectedPosition).getName() + ".jpg");
                Bitmap bmp;
                //if (picFile.exists()) {
                    //如果文件已经下载 则直接使用
                //    bmp = BitmapFactory.decodeFile(picFile.getAbsolutePath());
                //} else {
                    //否则使用缓存里的文件
                    bmp = BitmapFactory.decodeFile(mDownloadFiles.get(selectedPosition).getAbsolutePath());
                //}
                try {
                    WallpaperManager wallpaperManager = (WallpaperManager) mActivity.getSystemService(
                            Context.WALLPAPER_SERVICE);
                    if (bmp != null) {
                        wallpaperManager.setBitmap(bmp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPath = mDownloadFiles.get(selectedPosition).getAbsolutePath();
                String newPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" +
                        mDownloadFiles.get(selectedPosition).getName() + ".jpg";
                mIImageDownloader.copyFile(oldPath, newPath);

                MediaScannerConnection.scanFile(mActivity,
                        arrayOf(newPath),
                        arrayOf("image/jpeg"),
                        new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                        Snackbar.make(mViewPager, "图片保存成功", Snackbar.LENGTH_SHORT).show();
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
        String[] s = new String[]{string};
        return s;
    }

    public void create() {
        mDialog.show();
        mViews = new ArrayList<>();
        adapter = new WallpaperPagerAdapter(mViews, mDialog);
        if (mStatus == URLS) {
            for (String url : mUrls) {
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File downLoadFile;
                        try {
                            downLoadFile = mIImageDownloader.downLoad(url, mActivity);
                            for (int i = 0; i < mUrls.size(); i++) {
                                if (mUrls.get(i) == url) {
                                    mDownloadFiles.add(i, downLoadFile);
                                    break;
                                }
                            }
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(mActivity).load(downLoadFile).into(wallpaperView);//imageView.setImage(ImageSource.uri(Uri.fromFile(downLoadFile)));
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

