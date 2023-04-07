package com.example.floating;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


public class ZoomView extends RelativeLayout {
    // 属性变量
    private float translationX; // 移动X
    private float translationY; // 移动Y
    private float scale = 1; // 伸缩比例
//  private float rotation; // 旋转角度
//  private float degree;

    // 移动过程中临时变量
    private float actionX;
    private float actionY;
    private float spacing;

    private int moveType; // 0=未选择，1=拖动，2=缩放

    /**
     * 初始化高度
     */
    private int originalHeight = 0;

    /**
     * 初始化宽度
     */
    private int originalWidth = 0;
    private int screenHeight = 0;
    private int screenWidth = 0;

    /**
     * 最低缩小比例>=0.1
     */
    private float minimumScale = 0.4f;

    private Context mContext;


    public ZoomView(Context context) {
        this(context, null);
        mContext = context;
    }

    public ZoomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public ZoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("TAG", "onDraw ");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.e("TAG", "onLayout ");

        if (originalHeight == 0) {
            originalHeight = getMeasuredHeight();
            originalWidth = getMeasuredWidth();

            if (mContext != null) {
                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                screenHeight = metrics.widthPixels;
                screenWidth = metrics.heightPixels;
            }

            Log.e("TAG", "onLayout getMeasuredHeight = " + originalHeight);
            Log.e("TAG", "onLayout getMeasuredWidth = " + originalWidth);
            Log.e("TAG", "onLayout screenHeight = " + screenHeight);
            Log.e("TAG", "onLayout screenWidth = " + screenWidth);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e("TAG", "onMeasure ");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {


        Log.e("TAG", " onInterceptTouchEvent  ");
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                moveType = 1;
                actionX = event.getRawX();//ldw: 第一次的接触点;
                actionY = event.getRawY();
                Log.e("TAG", ">>>>>>>>>>>ACTION_DOWN. actionX = " + actionX);
                Log.e("TAG", ">>>>>>>>>>>ACTION_DOWN. actionY = " + actionY);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                moveType = 2;
                spacing = getSpacing(event);
//                degree = getDegree(event);
                break;
            case MotionEvent.ACTION_MOVE:

                if (moveType == 1) {

                    translationX = translationX + event.getRawX() - actionX;
                    ;
                    translationY = translationY + event.getRawY() - actionY;

                    //父view先变换位置就行了;
                    if (translationListener != null) {
                        translationListener.translation(translationX, translationY);
                    }

                    actionX = event.getRawX();
                    actionY = event.getRawY();


                } else if (moveType == 2) {

                    scale = scale * getSpacing(event) / spacing;


                    //这里的意思是, 让放大的最大高度不超过屏幕的宽度, 这样, 放大view后, 就不会超出屏幕, 无论横屏还是竖屏
                    int maxZoomViewHeight = screenHeight > screenWidth ? screenWidth : screenHeight;

                    //限制最小缩放比例
                    if (scale < getMinimumScale()) {
                        scale = getMinimumScale();
                    }


                    Log.e("TAG ", "1 scale = " + scale);
                    if (originalHeight * scale >= maxZoomViewHeight && originalHeight != 0) {
                        //这里需要乘以float, 因为int 除以float得出是int的值, 数据误差太大;
                        scale = maxZoomViewHeight * 1.0f / originalHeight * 1.0f;
                        Log.e("TAG ", "2 scale = " + scale);
                    }

                    //核心是先改变父类的位置, 然后再缩小子类, 关键是父类要用约束布局!!
                    if (translationListener != null) {
                        translationListener.scale(scale);
                    }

                    setScaleX(scale);
                    setScaleY(scale);

                    //下面部分是旋转的功能, 暂时不需要了;
//                    rotation = rotation + getDegree(event) - degree;
//                    if (rotation > 360) {
//                        rotation = rotation - 360;
//                    }
//                    if (rotation < -360) {
//                        rotation = rotation + 360;
//                    }
//                    setRotation(rotation);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                moveType = 0;
        }

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


//        Log.e("TAG", " onTouchEvent  ");
//
//        Log.e("TAG", " event.getAction()  = " + event.getAction());
//        Log.e("TAG", " event.getAction() & MotionEvent.ACTION_MASK = " + (event.getAction() & MotionEvent.ACTION_MASK));
//
//        switch (event.getAction() & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN:
//                moveType = 1;
//                actionX = event.getRawX();//ldw: 第一次的接触点;
//                actionY = event.getRawY();
//                Log.e("TAG", ">>>>>>>>>>>ACTION_DOWN. actionX = " + actionX);
//                Log.e("TAG", ">>>>>>>>>>>ACTION_DOWN. actionY = " + actionY);
//                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                moveType = 2;
//                spacing = getSpacing(event);
//                degree = getDegree(event);
//                break;
//            case MotionEvent.ACTION_MOVE:
//
//
//                if (moveType == 1) {
//
//                    translationX = translationX + event.getRawX() - actionX;//liudongwen 平移后的点减去第一次的接触点;
//                    translationY = translationY + event.getRawY() - actionY;
//
//                    //父view先变换位置就行了;
//                    if (translationListener != null) {
//                        translationListener.translation(translationX, translationY);
//                    }
//
//                    actionX = event.getRawX();
//                    actionY = event.getRawY();
//
//
//                } else if (moveType == 2) {
//
//                    scale = scale * getSpacing(event) / spacing;
//
//                    //核心是先改变父类的位置, 然后再缩小子类, 关键是父类要用约束布局!!
//                    if (translationListener != null) {
//                        translationListener.scale(actionX, actionY, scale);
//                    }
//
//
//                    setScaleX(scale);
//                    setScaleY(scale);
//
//
//                    rotation = rotation + getDegree(event) - degree;
//                    if (rotation > 360) {
//                        rotation = rotation - 360;
//                    }
//                    if (rotation < -360) {
//                        rotation = rotation + 360;
//                    }
////                    setRotation(rotation);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_POINTER_UP:
//                moveType = 0;
//        }
        return super.onTouchEvent(event);
    }


    TranslationListener translationListener;

    public void setTranslationListener(TranslationListener translationListener) {
        this.translationListener = translationListener;
    }

    public interface TranslationListener {

        void translation(float actionX, float actionY);

        void scale(float scale);
    }


    // 触碰两点间距离
    private float getSpacing(MotionEvent event) {
        //通过三角函数得到两点间的距离
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取旋转角度
    private float getDegree(MotionEvent event) {
        //得到两个手指间的旋转角度
        double delta_x = event.getX(0) - event.getX(1);
        double delta_y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }


    public float getMinimumScale() {
        return minimumScale;
    }

    public void setMinimumScale(float minimumScale) {
        if (minimumScale < 0.1f) {
            minimumScale = 0.1f;
        }
        this.minimumScale = minimumScale;
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    public int getOriginalHeight() {
        return originalHeight;
    }
}

