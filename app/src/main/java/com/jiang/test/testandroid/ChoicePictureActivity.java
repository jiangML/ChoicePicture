package com.jiang.test.testandroid;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/26.
 */
public class ChoicePictureActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private static final  int SCAN_OK=1;
    private RecycleFolderAdapter adapter;
    private RecyclerView rl;
    private List<String>  allImage=new ArrayList<>();
    private Map<String,View> map=new HashMap<>();
    private TextView tv_ok;



    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SCAN_OK:
                    init();
                    break;
                default:
                    break;
            }
            if (dialog!=null&&dialog.isShowing())
                dialog.dismiss();
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choice_picture_activity);
        rl=(RecyclerView)findViewById(R.id.rl);
        tv_ok=(TextView)findViewById(R.id.tv_ok);
        startGetImage();
    }


    /**
     * 获取手机中SD卡中的所有图片
     * @return
     */
    private void  getImages()
    {
        Uri imageUri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver=this.getContentResolver();
        Cursor cursor=resolver.query(imageUri,null,
                MediaStore.Images.Media.MIME_TYPE+"=? or "+MediaStore.Images.Media.MIME_TYPE+"=?",new String[]{"image/jpeg","image/png"},MediaStore.Images.Media.DATE_MODIFIED);

        while (cursor.moveToNext())
        {
            String path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            allImage.add(path);
        }
        cursor.close();
    }


    /**
     * 开始扫描图片
     */
   public void startGetImage()
   {
       if(dialog==null)
       {
           dialog=ProgressDialog.show(this,null,"正在扫描...");
       }else{
           dialog.show();
       }
       new Thread(){
           @Override
           public void run() {
               super.run();
               getImages();
               Message msg=handler.obtainMessage();
               msg.what=SCAN_OK;
               msg.sendToTarget();
           }
       }.start();
   }

  private void init()
  {
      GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3);
      rl.setLayoutManager(gridLayoutManager);
      adapter=new RecycleFolderAdapter(allImage,this);
      adapter.setListener(new RecycleFolderAdapter.OnItemTouchListener() {
          @Override
          public void onItemTouch(View view, int position,String uri) {
              if(!map.containsKey(uri))
              {
                  view.setAlpha(0.3f);
                  map.put(uri,view);
              }else{
                  view.setAlpha(1f);
                  map.remove(uri);
              }
              tv_ok.setText("完成("+map.size()+")");
          }
      });
      rl.setAdapter(adapter);
  }




}
