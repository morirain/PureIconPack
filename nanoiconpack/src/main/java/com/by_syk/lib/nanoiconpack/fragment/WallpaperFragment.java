/*
 * Copyright 2017 By_syk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.by_syk.lib.nanoiconpack.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.by_syk.lib.nanoiconpack.MainActivity;
import com.by_syk.lib.nanoiconpack.R;
import com.by_syk.lib.nanoiconpack.bean.AppBean;
import com.by_syk.lib.nanoiconpack.bean.WallpaperBean;
import com.by_syk.lib.nanoiconpack.util.ExtraUtil;
import com.by_syk.lib.nanoiconpack.util.adapter.WallpaperAdapter;
import com.by_syk.lib.nanoiconpack.widget.DividerItemDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


/**
 * Created by morirain on 2018-04-21.
 */

public class WallpaperFragment extends Fragment implements View.OnClickListener/* implements IconsFragment.OnLoadDoneListener*/ {

    private List<WallpaperBean> dataList = new ArrayList<>();

    private int pageId = 0;

    private LinearLayoutManager layoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;

    private View contentView;

    private String requestUrl = "https://raw.githubusercontent.com/morirain/PureIconPack/Stable/wallpaper/wallpaper.json";

    private WallpaperAdapter wallpaperAdapter;

    private OnLoadDoneListener onLoadDoneListener;

    public interface OnLoadDoneListener {
        void onLoadDone(int pageId, int sum);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof WallpaperFragment.OnLoadDoneListener) {
            onLoadDoneListener = (WallpaperFragment.OnLoadDoneListener) activity;
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_wallpaper, container, false);

            (new LoadAppsTask()).execute(false);
            init();
        }
        return contentView;
    }

    /*morirain*/




    private void init() {
        pageId = getArguments().getInt("pageId");
        initAdapter();
        initRecycler();
        initSwipeRefresh();

    }


    private void initAdapter() {
        wallpaperAdapter = new WallpaperAdapter(getContext());
        wallpaperAdapter.setOnItemClickListener(new WallpaperAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos, WallpaperBean bean) {

            }
        });
    }

    private void initRecycler() {
        layoutManager = new LinearLayoutManager(getContext());

        FastScrollRecyclerView recyclerView = (FastScrollRecyclerView) contentView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                /*if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (lazyLoadTask == null) {
                        lazyLoadTask = new ReqStatsFragment.LazyLoadTask();
                        lazyLoadTask.execute(layoutManager.findFirstVisibleItemPosition(),
                                layoutManager.findLastVisibleItemPosition());
                    }
                } else if (lazyLoadTask != null) {
                    lazyLoadTask.cancel(true);
                    lazyLoadTask = null;
                }*/
            }
        });
        recyclerView.setStateChangeListener(new OnFastScrollStateChangeListener() {
            @Override
            public void onFastScrollStart() {
                /*if (lazyLoadTask != null) {
                    lazyLoadTask.cancel(true);
                    lazyLoadTask = null;
                }*/
            }

            @Override
            public void onFastScrollStop() {
                /*if (lazyLoadTask == null) {
                    lazyLoadTask = new ReqStatsFragment.LazyLoadTask();
                    lazyLoadTask.execute(layoutManager.findFirstVisibleItemPosition(),
                            layoutManager.findLastVisibleItemPosition());
                }*/
            }
        });

        recyclerView.setAdapter(wallpaperAdapter);
    }

    private void initSwipeRefresh() {
        swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(ExtraUtil.fetchColor(getContext(), R.attr.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                (new LoadAppsTask()).execute(true);
            }
        });
    }

    public static WallpaperFragment newInstance(int id) {
        WallpaperFragment fragment = new WallpaperFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("pageId", id);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.rate_button) {
            Uri uri = Uri.parse("market://details?id=" + MainActivity.PACKAGE_NAME);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }




    private class LoadAppsTask extends AsyncTask<Boolean, Integer, List<WallpaperBean>> {


        private void requestWallpaper() {
            try{
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(requestUrl)
                        .build();
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                parseJSONWithGSON(responseData);
            }catch (Exception e){
                e.printStackTrace();
            }
            /*HttpUtil.sendOkHttpRequest(requestUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //对异常情况进行处理
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);
                }
            });*/
        }

        private void parseJSONWithGSON(String jsonData){
            Gson gson = new Gson();
            dataList = gson.fromJson(jsonData, new TypeToken<List<WallpaperBean>>(){}.getType());
            //for (WallpaperBean wallpaper : wallpaperList){
            //if (wallpaper.getNumWallpaper() >= 0) {
            //if (onLoadDoneListener != null) {
            //    onLoadDoneListener.onLoadDone(pageId, wallpaper.getNumWallpaper());
            //}
            //    continue;
            //}
            //Log.e(TAG, "parseJSONWithGSON: "+wallpaper.getAuthor() );
            //Log.e(TAG, "parseJSONWithGSON: "+wallpaper.getUrl() );
            //Log.e(TAG, "parseJSONWithGSON: "+wallpaper.getThumbUrl() );
            //}
            //dataList = wallpaperList;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //retainedFragment = RetainedFragment.initRetainedFragment(getFragmentManager(), "app");
        }

        @Override
        protected List<WallpaperBean> doInBackground(Boolean... booleans) {
            boolean forceRefresh = booleans.length > 0 && booleans[0];
            /*if (!forceRefresh && retainedFragment.isAppListSaved()) {
                return retainedFragment.getAppList();
            }*/
            requestWallpaper();
            if (getContext() == null) {
                return dataList;
            }


            if (dataList.isEmpty()) {
                return dataList;
            }


            return dataList;
        }

        @Override
        protected void onPostExecute(List<WallpaperBean> list) {
            super.onPostExecute(list);


            //retainedFragment.setAppList(list);

            ((AVLoadingIndicatorView) contentView.findViewById(R.id.view_loading)).hide();
            wallpaperAdapter.loadDone(list);
            wallpaperAdapter.refresh(list);

            swipeRefreshLayout.setRefreshing(false);

            if (onLoadDoneListener != null) {
                onLoadDoneListener.onLoadDone(pageId, list.size());
            }
        }

    }


}
