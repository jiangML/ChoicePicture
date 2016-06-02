package com.jiang.test.testandroid.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/5/31.
 */
public class ZoomImageView extends ImageView  implements ScaleGestureDetector.OnScaleGestureListener,View.OnTouchListener,ViewTreeObserver.OnGlobalLayoutListener{

    //初始化缩放比例
    private float deafultScale=1.0f;
    //最大缩放比例
    public float maxScale=4.0f;
    //手势检测
    private ScaleGestureDetector mScaleGestureDetector=null;
    //缩放矩阵
    private Matrix matrix=new Matrix();

    //存放矩阵的9个值
    private float[] matrixValues=new float[9];

    //是否是第一次调用onGlobalLayout()
    private boolean isFirst=true;

    //检测双击事件
    private GestureDetector gestureDetector;

    public ZoomImageView(Context context) {
        super(context);
        init();
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private boolean isAutoScalIng=false;

    private void init()
    {
        mScaleGestureDetector=new ScaleGestureDetector(getContext(),this);
        setOnTouchListener(this);

        gestureDetector=new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                System.out.println("双击.....");
                if(isAutoScalIng)
                {
                    return true;
                }
                isAutoScalIng=true;
                float x=e.getX();
                float y=e.getY();

                //如果当前缩放比例为初始比例到最大放大比例一半之间 就放大为最大比例一半
                if(getScale()>=deafultScale&&getScale()<maxScale/2)
                {
                    ZoomImageView.this.postDelayed(new AutoScaleRunnable(maxScale/2,x,y),20);
                }else if(getScale()>maxScale/2&&getScale()<maxScale){

                    ZoomImageView.this.postDelayed(new AutoScaleRunnable(maxScale,x,y),20);
                }else{
                    ZoomImageView.this.postDelayed(new AutoScaleRunnable(deafultScale,x,y),20);
                }
                return true;
            }
        });

    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {

         float scale=getScale();
         float scaleFactor=detector.getScaleFactor();
         if(getDrawable()==null)
             return true;

        //当图片放大或者缩小时 计算缩放比例 注意这里缩小只对在放大了的时候才会缩小
         if((scale<maxScale&&scaleFactor>1.0f)||(scale>deafultScale&&scaleFactor<1.0f))
         {
             if(scaleFactor*scale<deafultScale)
             {
                 scaleFactor=deafultScale/scale;
             }
             if(scaleFactor*scale>maxScale)
             {
                  scaleFactor=maxScale/scale;
             }
             matrix.postScale(scaleFactor,scaleFactor,detector.getFocusX(),detector.getFocusY());
             checkScale();
             setImageMatrix(matrix);
         }

        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }


    private boolean canLeftRight=true;
    private boolean canTopBottom=true;
    private int lastPointerCount=0;
    private float lastX=0;
    private float lastY=0;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        //实现平移操作
        float x=0;
        float y=0;
        //获取触摸点的数量
        int pointerCoun=event.getPointerCount();
        for(int i=0;i<pointerCoun;i++)
        {
            x+=event.getX(i);
            y+=event.getY(i);
        }
        x=x/pointerCoun;
        y=y/pointerCoun;

        if(pointerCoun!=lastPointerCount)
        {
            lastX=x;
            lastY=y;
        }
        lastPointerCount=pointerCoun;

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                lastPointerCount=0;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx=x-lastX;
                float dy=y-lastY;
                move(dx,dy);
                lastX=x;
                lastY=y;
                break;
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount=0;
                break;
            case MotionEvent.ACTION_UP:
                lastPointerCount=0;
                break;
            default:
                lastPointerCount=0;
                break;
        }
        return true;
    }

    /**
     * 平移操作
     * @param dx
     * @param dy
     */
    private void move(float dx,float dy)
    {
        RectF rectF=getDrawableRectf();
        canLeftRight=canTopBottom=true;//每次都要设置为true
        if(getDrawable()!=null)
        {
            if(rectF.width()<getWidth())
            {
                dx=0;
                canLeftRight=false;
            }
            if(rectF.height()<getHeight())
            {
                dy=0;
                canTopBottom=false;
            }
            matrix.postTranslate(dx, dy);
            checkDrawableBounds();
            setImageMatrix(matrix);
        }
    }

    /**
     * 检测图片移动时 边界是否出现白边，如果出现就处理。
     */
    private void checkDrawableBounds()
    {
       RectF rectF=getDrawableRectf();
       float offsetX=0;
       float offsetY=0;
       //当高大于控件高并且在上方出现白边时
       if(rectF.top>0&&canTopBottom)
       {
           offsetY=-rectF.top;
       }
        //当下面出现白边时
       if(rectF.bottom<getHeight()&&canTopBottom)
       {
           offsetY=getHeight()- rectF.bottom;
       }

        //当左边出现白边时
       if(rectF.left>0&&canLeftRight)
       {
           offsetX=-rectF.left;
       }
        //当右边出现白边时
       if(rectF.right<getWidth()&&canLeftRight)
       {
          offsetX=getWidth()-rectF.right;
       }
       matrix.postTranslate(offsetX,offsetY);
        setImageMatrix(matrix);
    }


    @Override
    public void onGlobalLayout() {

        if(!isFirst)
            return;
        Drawable drawable=getDrawable();
        if(drawable==null)
            return;
        int width=getWidth();
        int height=getHeight();

        //获取图片宽高
        int drawableWidth=drawable.getIntrinsicWidth();
        int drawableHeight=drawable.getIntrinsicHeight();
        float scale=1.0f;//默认缩放比例
        //当图片宽度大于控件宽度并且图片高度小于控件高度时
        if(drawableWidth>width&&drawableHeight<height)
        {
            scale=width*1.0f/drawableWidth;
        }
        //当图片高度大于控件高度并且图片宽度小于控件宽度时
        if(drawableHeight>height&&drawableWidth<width)
        {
            scale=height*1.0f/drawableHeight;
        }

        //当图片的宽高都大于控件的宽高时
        if(drawableHeight>height&&drawableWidth>width)
        {
            scale=Math.min(width*1.0f/drawableWidth,height*1.0f/drawableHeight);
        }
        deafultScale=scale;//设置第一次显示时的缩放比例
        //设置图片居中显示
        matrix.postTranslate((width-drawableWidth)/2,(height-drawableHeight)/2);
        matrix.postScale(scale,scale,getWidth()/2,getHeight()/2);
        setImageMatrix(matrix);
        isFirst=false;
    }

    /**
     * 获取当前的缩放比例
     * @return
     */
    private float getScale()
    {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }


    /**
     * 获取图片在控件中的布局位置
     * @return
     */
    private RectF getDrawableRectf()
    {
        Matrix matrix1=matrix;
        RectF rectF=new RectF();
        Drawable drawable=getDrawable();
        if(drawable!=null)
        {
            rectF.set(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            matrix1.mapRect(rectF);
        }
        return rectF;
    }

    private void checkScale() {
        RectF rectf = getDrawableRectf();
        float offsetX = 0;
        float offsetY = 0;

        int width = getWidth();
        int height = getHeight();

        //当图片宽度大于控件宽度时
        if (rectf.width() >= width) {
           if(rectf.left>0)
           {
               offsetX=-rectf.left;
           }
           if (rectf.right<width)
           {
               offsetX=width-rectf.right;
           }
//            if(rectf.left<0)
//            {
//                offsetX=Math.abs(rectf.left)-(rectf.width()-width)*1.0f/2;
//            }
//            if(rectf.left>=0)
//            {
//                offsetX=-(rectf.left+(rectf.width()-width)*1.0f/2);
//            }
        }

        if (rectf.height() >= height) {
            if(rectf.top>0)
            {
                offsetY=-rectf.top;
            }
            if(rectf.height()<height)
            {
                offsetY=height-rectf.bottom;
            }

//            if(rectf.top>=0)
//            {
//                offsetY=-(rectf.top+(rectf.height()-height)*1.0f/2);
//            }
//            if(rectf.top<0)
//            {
//                offsetY=Math.abs(rectf.top)-(rectf.height()-height)*1.0f/2;
//            }
        }

        if (rectf.width() < width)
        {
           offsetX=width*0.5f-rectf.right+0.5f*rectf.width();
        }
        if(rectf.height()<height)
        {
           offsetY=height*0.5f-rectf.bottom+0.5f*rectf.height();
        }
        matrix.postTranslate(offsetX,offsetY);
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    private class AutoScaleRunnable implements Runnable{

        private float targetScale;
        private float x;
        private float y;
        private float tempScale=getScale();
        public AutoScaleRunnable(float targetScale, float x, float y)
        {
            this.targetScale=targetScale;
            this.x=x;
            this.y=y;
            if(getScale()<targetScale)
            {
                tempScale=1.1f;
            }else{
                tempScale=0.9f;
            }
        }

        @Override
        public void run() {
           matrix.postScale(tempScale,tempScale,x,y);
           checkScale();
           setImageMatrix(matrix);

           if(((tempScale>1f)&&(getScale()<targetScale))||((tempScale<1f)&&(getScale()>tempScale)))
           {
            ZoomImageView.this.postDelayed(this,20);
           }else {
               float scale=targetScale/getScale();
               matrix.postScale(scale,scale,x,y);
               checkScale();
               setImageMatrix(matrix);
               isAutoScalIng=false;
           }
        }
    }





}
