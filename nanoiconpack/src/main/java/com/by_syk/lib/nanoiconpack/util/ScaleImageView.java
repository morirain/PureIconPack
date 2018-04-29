package com.by_syk.lib.nanoiconpack.util;


import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.by_syk.lib.nanoiconpack.R;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    private ImageView mSetWallpaper;//删除图片的按钮
    private ImageView mDownload;//保存图片到本地的按钮
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
        ImageView close = (ImageView) relativeLayout.findViewById(R.id.scale_image_close);
        mSetWallpaper = (ImageView) relativeLayout.findViewById(R.id.scale_image_set_wallpaper);
        mDownload = (ImageView) relativeLayout.findViewById(R.id.scale_image_save);
        tvImageCount = (TextView) relativeLayout.findViewById(R.id.scale_image_count);
        mViewPager = (ViewPager) relativeLayout.findViewById(R.id.scale_image_view_pager);

        mDialog = new Dialog(mActivity, R.style.WallpaperFullscreen);
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
                int size = mViews.size();
                mFiles.remove(selectedPosition);
                if (listener != null) {
                    listener.onDelete(selectedPosition);
                }
                mViewPager.removeView(mViews.remove(selectedPosition));
                if (selectedPosition != size) {
                    int position = selectedPosition + 1;
                    String text = position + "/" + mViews.size();
                    tvImageCount.setText(text);
                }
                adapter.notifyDataSetChanged();
            }
        });

        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MediaStore.Images.Media.insertImage(mActivity.getContentResolver(),
                            mDownloadFiles.get(selectedPosition).getAbsolutePath(),
                            mDownloadFiles.get(selectedPosition).getName(), null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //Snackbar.make(mViewPager, "图片保存成功", Snackbar.LENGTH_SHORT).show();
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

    public void create() {
        mDialog.show();
        mViews = new ArrayList<>();
        adapter = new WallpaperPagerAdapter(mViews, mDialog);
        if (mStatus == URLS) {
            for (String url : mUrls) {
                FrameLayout frameLayout = (FrameLayout) mActivity.getLayoutInflater().inflate(R.layout.item_wallpaper_pager, null);
                frameLayout.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.slide_bottom_to_top));
                SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) frameLayout.findViewById(R.id.scale_image_view);
                mViews.add(frameLayout);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File downLoadFile;
                        try {
                            downLoadFile = mIImageDownloader.downLoad(url, mActivity);
                            mDownloadFiles.add(downLoadFile);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImage(ImageSource.uri(Uri.fromFile(downLoadFile)));
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
                mViews.add(frameLayout);
                imageView.setImage(ImageSource.uri(Uri.fromFile(file)));
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

