package com.by_syk.lib.nanoiconpack.bean;

public class WallpaperBean {

    /**
     * numWallpaper : 3
     * author : Annie Spratt
     * url : https://raw.githubusercontent.com/morirain/PureIconPack/Stable/wallpaper/annie-spratt-230184-unsplash.jpg
     * thumbUrl : https://raw.githubusercontent.com/morirain/PureIconPack/Stable/wallpaper/thumb/annie-spratt-230184-unsplash.jpg
     * source : https://unsplash.com/photos/YGBaA4UdoLY
     */

    private static int numWallpaper;
    private String author;
    private String url;
    private String thumbUrl;
    private String source;

    public static int getNumWallpaper() {
        //请求接口
        String url = "https://api.douban.com/v2/book/1220562";
        
        return numWallpaper;
    }

    /*public static void setNumWallpaper(int numWallpaper) {
        numWallpaper = numWallpaper;
    }*/

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
