package com.jiang.test.testandroid.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import com.jiang.test.testandroid.R;

/**
 * Created by Administrator on 2016/6/1.
 */
public class BlueToothActivity extends Activity {

    private ProgressBar bar;
    private RecyclerView rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        bar=(ProgressBar)findViewById(R.id.pb);
        rl=(RecyclerView)findViewById(R.id.recyxler_view);
    }
    


}
