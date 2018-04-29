package com.by_syk.lib.nanoiconpack.util;


import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

import static android.content.ContentValues.TAG;

/**
 * Created by morirain on 2018/4/29.
 * E-Mail Address：morirain.dev@outlook.com
 */


public class ScaleImageView {

    private static final byte URLS = 0;//网络查看状态
    private static final byte FILES = 1;//本地查看状态
    private byte status;//用来表示当前大图查看器的状态

    private Activity activity;

    private List<String> urls;//网络查看状态中传入的要查看的图片的Url的List
    private List<File> files;//本地查看状态中传入的要查看的图片对应的file对象的List
    private List<File> downloadFiles;//网络查看状态中从Url下载下来的图片对应的Url的List

    private int selectedPosition;//表示当前被选中的ViewPager的item的位置

    private Dialog dialog;//用于承载整个大图查看器的Dialog

    private ImageView setWallpaper;//删除图片的按钮
    private ImageView download;//保存图片到本地的按钮
    private TextView imageCount;//用于显示当前正在查看第几张图片的TextView
    private ViewPager viewPager;

    private List<View> views;//ViewPager适配器的数据源
    private WallpaperPagerAdapter adapter;

    private OnDeleteItemListener listener;
    private int startPosition;//打开大图查看器时，想要查看的ViewPager的item的位置

    public ScaleImageView(Activity activity) {
        this.activity = activity;
        init();
    }

    public void setUrls(List<String> urls, int startPosition) {
        if (this.urls == null) {
            this.urls = new ArrayList<>();
        } else {
            this.urls.clear();
        }
        this.urls.addAll(urls);
        status = URLS;
        /*delete.setVisibility(View.GONE);
        if (downloadFiles == null) {
            downloadFiles = new ArrayList<>();
        } else {
            downloadFiles.clear();
        }*/
        this.startPosition = startPosition++;
        String text = startPosition + "/" + urls.size();
        imageCount.setText(text);
    }

    public void setFiles(List<File> files, int startPosition) {
        if (this.files == null) {
            this.files = new LinkedList<>();
        } else {
            this.files.clear();
        }
        this.files.addAll(files);
        status = FILES;
        download.setVisibility(View.GONE);
        this.startPosition = startPosition++;
        String text = startPosition + "/" + files.size();
        imageCount.setText(text);
    }

    /*public void setOnDeleteItemListener(OnDeleteItemListener listener) {
        this.listener = listener;
    }*/

    private void init() {
        RelativeLayout relativeLayout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.dialog_wallpaper, null);
        ImageView close = (ImageView) relativeLayout.findViewById(R.id.scale_image_close);
        setWallpaper = (ImageView) relativeLayout.findViewById(R.id.scale_image_set_wallpaper);
        download = (ImageView) relativeLayout.findViewById(R.id.scale_image_save);
        imageCount = (TextView) relativeLayout.findViewById(R.id.scale_image_count);
        viewPager = (ViewPager) relativeLayout.findViewById(R.id.scale_image_view_pager);
        dialog = new Dialog(activity, R.style.WallpaperFullscreen);
        dialog.setContentView(relativeLayout);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //设置壁纸
        setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = views.size();
                files.remove(selectedPosition);
                if (listener != null) {
                    listener.onDelete(selectedPosition);
                }
                viewPager.removeView(views.remove(selectedPosition));
                if (selectedPosition != size) {
                    int position = selectedPosition + 1;
                    String text = position + "/" + views.size();
                    imageCount.setText(text);
                }
                adapter.notifyDataSetChanged();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MediaStore.Images.Media.insertImage(activity.getContentResolver(),
                            downloadFiles.get(selectedPosition).getAbsolutePath(),
                            downloadFiles.get(selectedPosition).getName(), null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Snackbar.make(viewPager, "图片保存成功", Snackbar.LENGTH_SHORT).show();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedPosition = position;
                String text = ++position + "/" + views.size();
                imageCount.setText(text);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void create() {
        dialog.show();
        views = new ArrayList<>();
        adapter = new WallpaperPagerAdapter(views, dialog);
        if (status == URLS) {
            for (final String url : urls) {
                FrameLayout frameLayout = (FrameLayout) activity.getLayoutInflater().inflate(R.layout.item_wallpaper_pager, null);
                final SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) frameLayout.findViewById(R.id.scale_image_view);
                views.add(frameLayout);
                Log.e(TAG, "create: "+ url);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File downLoadFile;
                        try {
                            downLoadFile = Glide.with(activity)
                                    .load(url)
                                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                    .get();
                            downloadFiles.add(downLoadFile);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImage(ImageSource.uri(Uri.fromFile(downLoadFile)));
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                //IOThread.getSingleThread().execute(() -> {

                //});
            }
            viewPager.setAdapter(adapter);
        } else if (status == FILES) {
            for (File file : files) {
                FrameLayout frameLayout = (FrameLayout) activity.getLayoutInflater().inflate(R.layout.item_wallpaper_pager, null);
                SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) frameLayout.findViewById(R.id.scale_image_view);
                views.add(frameLayout);
                imageView.setImage(ImageSource.uri(Uri.fromFile(file)));
            }
            viewPager.setAdapter(adapter);
        }
        viewPager.setCurrentItem(startPosition);
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