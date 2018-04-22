package com.by_syk.lib.nanoiconpack.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WallpaperBean {

    /**
     * author : Annie Spratt
     * url : https://raw.githubusercontent.com/morirain/PureIconPack/Stable/wallpaper/annie-spratt-230184-unsplash.jpg
     * thumbUrl : https://raw.githubusercontent.com/morirain/PureIconPack/Stable/wallpaper/thumb/annie-spratt-230184-unsplash.jpg
     * source : https://unsplash.com/photos/YGBaA4UdoLY
     */

    private String author;
    private String url;
    private String thumbUrl;
    private String source;

    public static List<WallpaperBean> arrayWallpaperBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<WallpaperBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
