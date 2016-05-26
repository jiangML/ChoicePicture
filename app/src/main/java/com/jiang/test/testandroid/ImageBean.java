package com.jiang.test.testandroid;

/**
 * Created by Administrator on 2016/5/26.
 * 每个文件夹里面的图片bean
 */
public class ImageBean {

    //文件夹里面第一张图片的路径
    private String firstImagUrl;

    //文件夹名字
    private String folderName;

    //文件夹里面的图片数量
    private int count;

    public String getFirstImagUrl() {
        return firstImagUrl;
    }

    public void setFirstImagUrl(String firstImagUrl) {
        this.firstImagUrl = firstImagUrl;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
