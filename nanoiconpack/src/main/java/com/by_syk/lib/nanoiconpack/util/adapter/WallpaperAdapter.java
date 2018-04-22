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

package com.by_syk.lib.nanoiconpack.util.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.by_syk.lib.nanoiconpack.R;
import com.by_syk.lib.nanoiconpack.bean.AppBean;
import com.by_syk.lib.nanoiconpack.bean.WallpaperBean;
import com.by_syk.lib.nanoiconpack.util.ExtraUtil;
import com.by_syk.lib.nanoiconpack.util.PkgUtil;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder> {
    private Context context;

    private LayoutInflater layoutInflater;

    private List<WallpaperBean> dataList = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(int pos, WallpaperBean bean);
    }

    public WallpaperAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    //public void loadDone(List<WallpaperBean> dataList) {
    //    this.dataList = dataList;
    //}

    @Override
    public WallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = layoutInflater.inflate(R.layout.item_wallpaper, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WallpaperViewHolder holder, int position) {
        WallpaperBean bean = dataList.get(position);
        Glide.with(context).load(bean.getThumbUrl()).into(holder.thumbImage);
        //Log.e(TAG, "onBindViewHolder: "+ dataList);

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition();
                    onItemClickListener.onClick(pos, dataList.get(pos));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Nullable
    public WallpaperBean getItem(int pos) {
        if (pos >= 0 && pos < dataList.size()) {
            return dataList.get(pos);
        }
        return null;
    }

    public void refresh(List<WallpaperBean> dataList) {
        if (dataList != null) {
            this.dataList.clear();
            this.dataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    public void remove(int pos) {
        if (pos < 0 || pos >= dataList.size()) {
            return;
        }
        dataList.remove(pos);
        notifyItemRemoved(pos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class WallpaperViewHolder extends RecyclerView.ViewHolder {
        //CardView cardView;
        ImageView thumbImage;

        public WallpaperViewHolder(View view) {
            super(view);

            //cardView = (CardView) view;
            thumbImage = (ImageView) view.findViewById(R.id.wallpaper_image);
        }
    }
}
