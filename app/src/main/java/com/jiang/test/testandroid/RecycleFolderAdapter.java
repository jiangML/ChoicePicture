package com.jiang.test.testandroid;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/26.
 */
public class RecycleFolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private LayoutInflater inflater;
    List<String> data=new ArrayList<>();

    public interface OnItemTouchListener{
        void onItemTouch(View view,int position,String uri);
    }
    private OnItemTouchListener listener;

    public RecycleFolderAdapter(List<String> data,Context context)
    {
        this.data=data;
        inflater=LayoutInflater.from(context);
        this.context=context;
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FolderHolder(inflater.inflate(R.layout.item_recycle_folder,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            ((FolderHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                        listener.onItemTouch(v,position,data.get(position));
                }
            });
            Glide.with(context).load(data.get(position)).into(((FolderHolder) holder).iv);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class  FolderHolder extends RecyclerView.ViewHolder
    {
        ImageView iv;
        public FolderHolder(View itemView) {
            super(itemView);
            iv=(ImageView)itemView.findViewById(R.id.iv_folder);
        }
    }

   public void setListener(OnItemTouchListener listener)
   {
       this.listener=listener;
   }

}
