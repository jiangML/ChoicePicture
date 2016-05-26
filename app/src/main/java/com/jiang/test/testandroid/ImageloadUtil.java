package com.jiang.test.testandroid;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


/**
 * Created by Administrator on 2016/5/26.
 */
public class ImageloadUtil implements ImageLoadI {

    private Context context;
    public ImageloadUtil(Context context)
    {
        this.context=context;
    }

    @Override
    public void loadImage(ImageView iv, String url) {
        Glide.with(context).load(url).into(iv);
    }
}
