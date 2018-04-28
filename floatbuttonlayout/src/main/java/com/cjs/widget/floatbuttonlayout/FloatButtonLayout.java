package com.cjs.widget.floatbuttonlayout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * 描述:悬浮按钮布局<br>
 * <li>支持三星s系列手机手动开启隐藏导航栏适配
 * <li>支持页面全屏非全屏适配
 * <p>
 * 作者:陈俊森
 * 创建时间:2018年04月26日 15:59
 * 邮箱:chenjunsen@outlook.com
 *
 * @version 1.0
 */
public class FloatButtonLayout extends FrameLayout {

    /**
     * 默认的滑动与点击的零界值
     */
    private static final int DEFAULT_OFFSET_IS_CLICK = 10;

    /**
     * 用于判断是滑动还是点击的零界判定值，单位像素
     */
    private int offset_is_click = DEFAULT_OFFSET_IS_CLICK;

    /**
     * 按钮的控件
     */
    private View floatView;

    /**
     * 悬浮按钮布局的监听器
     */
    private FloatButtonLayoutListener mFloatButtonLayoutListener;

    /**
     * 悬浮按钮吸屏监听器
     */
    private FloatButtonSuckScreenListener mFloatButtonSuckScreenListener;

    /**
     * 是否允许滑动按钮移动到屏幕外侧
     */
    private boolean isAllowMoveBeyondScreen = false;

    /**
     * 吸屏方向
     */
    private int suckScreenDirection;

    /**
     * 按钮所在的Activity
     */
    private Activity mActivity;

    private int statusBarHeight, layoutHeight, layoutWidth;

    public FloatButtonLayout(@NonNull Context context) {
        super(context);
        mActivity = (Activity) context;
        initView(context, null);
    }

    public FloatButtonLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
        initView(context, attrs);
    }

    public FloatButtonLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = (Activity) context;
        initView(context, attrs);
    }

    @TargetApi(21)
    public FloatButtonLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mActivity = (Activity) context;
        initView(context, attrs);
    }

    /**
     * 获取虚浮按钮控件
     *
     * @return
     */
    public View getFloatView() {
        return floatView;
    }

    /**
     * 设置悬浮按钮监听器
     *
     * @param floatButtonLayoutListener
     */
    public void setFloatButtonLayoutListener(FloatButtonLayoutListener floatButtonLayoutListener) {
        mFloatButtonLayoutListener = floatButtonLayoutListener;
    }

    /**
     * 设置悬浮按钮吸屏监听器
     *
     * @param floatButtonSuckScreenListener
     */
    public void setFloatButtonSuckScreenListener(FloatButtonSuckScreenListener floatButtonSuckScreenListener) {
        mFloatButtonSuckScreenListener = floatButtonSuckScreenListener;
    }

    /**
     * 设置用于判定悬浮按钮是否移动的偏移量，默认值是{@link #DEFAULT_OFFSET_IS_CLICK}
     *
     * @param offset_is_click 单位是像素
     */
    public void setOffset_is_click(int offset_is_click) {
        this.offset_is_click = offset_is_click;
    }

    /**
     * 设置是否允许按钮滑动到屏幕外侧
     *
     * @param allowMoveBeyondScreen
     */
    public void setAllowMoveBeyondScreen(boolean allowMoveBeyondScreen) {
        isAllowMoveBeyondScreen = allowMoveBeyondScreen;
    }

    /**
     * 设置按钮吸屏的方向
     *
     * @param suckScreenDirection 可用的参考值:<br>
     *                            <ul>
     *                            <li>{@link SuckScreenDirection#START}</li>
     *                            <li>{@link SuckScreenDirection#END}</li>
     *                            <li>{@link SuckScreenDirection#TOP}</li>
     *                            <li>{@link SuckScreenDirection#BOTTOM}</li>
     *                            <li>{@link SuckScreenDirection#NONE}</li>
     *                            <li>{@link SuckScreenDirection#ALL}</li>
     *                            <ul/>
     */
    public void setSuckScreenDirection(int suckScreenDirection) {
        this.suckScreenDirection = suckScreenDirection;
    }

    private void initView(@NonNull Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FloatButtonLayout);
        offset_is_click = array.getDimensionPixelOffset(R.styleable.FloatButtonLayout_offsetToClick, DEFAULT_OFFSET_IS_CLICK);
        isAllowMoveBeyondScreen = array.getBoolean(R.styleable.FloatButtonLayout_isAllowMoveBeyondScreen, false);
        suckScreenDirection = array.getInteger(R.styleable.FloatButtonLayout_suck_screen_direction, SuckScreenDirection.NONE);
        array.recycle();
    }

    private void resetLayoutDimension() {
        statusBarHeight = Tools.getScreenStatusBarHeight(mActivity);
//            LogKit.i("statusBar", "状态栏高度:" + statusBarHeight);
        int screenHeight = Tools.getDeviceScreenHeight(mActivity);
        int screenWidth = Tools.getDeviceScreenWidth(mActivity);
        layoutHeight = getHeight() > screenHeight ? screenHeight : getHeight();
        layoutWidth = getWidth() > screenWidth ? screenWidth : getWidth();

    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        resetLayoutDimension();
        int childCount = getChildCount();
        if (childCount == 0) {
            Button floatButton = new Button(getContext());
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.END);
            floatButton.setLayoutParams(lp);
            floatView = floatButton;
            addView(floatView);
        } else {
            floatView = getChildAt(childCount - 1);
        }
        floatView.setOnTouchListener(new FloatViewTouchListener(mActivity));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        /*Log.d("fbl", "导航栏高度:" + Tools.getBottomNavigationBarHeight(mActivity));
        Log.d("fbl", "父布局getTop:" + getTop() + " 父布局getLeft:" + getLeft() + " 父布局getBottom:" + getBottom() + " 父布局getRight:" + getRight());
        Log.d("fbl", "底部导航栏是否存在:" + Tools.isBottomNavigationBarExists(mActivity));
        Log.d("fbl","statusbarHeight:"+statusBarHeight);
        Log.d("fbl","layoutHeight:"+layoutHeight);
        Log.d("fbl","layoutWidth:"+layoutWidth);
        Log.d("fbl","getHeight:"+getHeight()+"   getWidth:"+getWidth());*/
        resetLayoutDimension();
    }

    /**
     * 悬浮按钮的触摸监听器
     */
    private class FloatViewTouchListener implements OnTouchListener {
        float lastDownX, lastDownY;
        float fingerDownX, fingerDownY;


        private Context mContext;

        public FloatViewTouchListener(Context context) {
            mContext = context;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //getRawX和getRawY方法是获取触摸点相对于整个屏幕，以左上角为坐标原点而获取到的坐标值

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //声明了两组值，用于记录手指的初始触摸点，不过第一组会在移动的时候不断更新，第二组则是保存的首次触摸点位置
                    fingerDownX = event.getRawX();
                    fingerDownY = event.getRawY();

                    lastDownX = event.getRawX();
                    lastDownY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float offsetX = (event.getRawX() - fingerDownX);//水平的位移
                    float offsetY = (event.getRawY() - fingerDownY);//垂直的位移

                    //getLeft和getTop是用于获取按钮的的左，上边界离父容器的左，上边界的距离
                    //分别加上自身的宽高后就得到了新的按钮右，下边界距离父容器的左，上边界的距离
                    float layoutLeft = v.getLeft() + offsetX;
                    float layoutTop = v.getTop() + offsetY;
                    if (!isAllowMoveBeyondScreen) {
                        layoutLeft = layoutLeft < 0 ? 0 : layoutLeft;
                        layoutTop = layoutTop < 0 ? 0 : layoutTop;
                    }
                    float layoutBottom = layoutTop + v.getMeasuredHeight();
                    float layoutRight = layoutLeft + v.getMeasuredWidth();
                    if (!isAllowMoveBeyondScreen) {
                        if (layoutBottom > layoutHeight) {
                            layoutBottom = layoutHeight;
                            layoutTop = layoutBottom - v.getMeasuredHeight();
                        }
                        if (layoutRight > layoutWidth) {
                            layoutRight = layoutWidth;
                            layoutLeft = layoutRight - v.getMeasuredWidth();
                        }
                    }

                    v.layout((int) layoutLeft, (int) layoutTop, (int) layoutRight, (int) layoutBottom);

                    //不断更新触摸点的初始位置
                    fingerDownX = event.getRawX();
                    fingerDownY = event.getRawY();

                    if (mFloatButtonLayoutListener != null) {
                        mFloatButtonLayoutListener.onMove(v, offsetX, offsetY);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    boolean isClickable =
                            !(Math.abs(event.getRawX() - lastDownX) >= offset_is_click || Math.abs(event.getRawY() - lastDownY) >= offset_is_click);
                    if (isClickable) {
                        v.performClick();
                        if (mFloatButtonLayoutListener != null) {
                            mFloatButtonLayoutListener.onClick(v);
                        }
                    } else {
                        if (mFloatButtonLayoutListener != null) {
                            mFloatButtonLayoutListener.onFingerUp(v);
                        }
                    }
                    if (suckScreenDirection != SuckScreenDirection.NONE) {
                        handleSuckScreen(v, suckScreenDirection);
                    }
                    break;
            }
            return true;
        }

        private void handleSuckScreen(View v, int direction) {
            //实际上ALL就是左上右下的综合
            if (direction == SuckScreenDirection.ALL) {
                direction = SuckScreenDirection.BOTTOM | SuckScreenDirection.TOP | SuckScreenDirection.END | SuckScreenDirection.START;
            }

            float centerVx = v.getMeasuredWidth() / 2 + v.getLeft();//按钮的中心X坐标
            float centerVy = v.getMeasuredHeight() / 2 + v.getTop();//按钮的中心Y坐标
            int layoutLeft = v.getLeft();
            int layoutTop = v.getTop();
            int layoutBottom = v.getBottom();
            int layoutRight = v.getRight();

            //位或运算进行吸屏方向的判断

            boolean isStart = (direction & SuckScreenDirection.START) == SuckScreenDirection.START;
            boolean isEnd = (direction & SuckScreenDirection.END) == SuckScreenDirection.END;
            boolean isTop = (direction & SuckScreenDirection.TOP) == SuckScreenDirection.TOP;
            boolean isBottom = (direction & SuckScreenDirection.BOTTOM) == SuckScreenDirection.BOTTOM;

            boolean isInLeft = centerVx <= layoutWidth / 2;
            boolean isInRight = centerVx > layoutWidth / 2;
            boolean isInTop = centerVy <= layoutHeight / 2;
            boolean isInBottom = centerVy > layoutHeight / 2;

            if (isStart) {
                if (isInLeft) {
                    layoutLeft = 0;
                    layoutRight = layoutLeft + v.getMeasuredWidth();
                }
            }
            if (isEnd) {
                if (isInRight) {
                    layoutRight = layoutWidth;
                    layoutLeft = layoutRight - v.getMeasuredWidth();
                }
            }
            if (isTop) {
                if (isInTop) {
                    layoutTop = 0;
                    layoutBottom = layoutTop + v.getMeasuredHeight();
                }
            }
            if (isBottom) {
                if (isInBottom) {
                    layoutBottom = layoutHeight;
                    layoutTop = layoutBottom - v.getMeasuredHeight();
                }
            }

            v.layout(layoutLeft, layoutTop, layoutRight, layoutBottom);


            if (isInLeft && isInTop && isStart && isTop) {
                if (mFloatButtonSuckScreenListener != null) {
                    mFloatButtonSuckScreenListener.onSuckToLeftTop(v);
                }
            } else if (isInLeft && isInBottom && isStart && isBottom) {
                if (mFloatButtonSuckScreenListener != null) {
                    mFloatButtonSuckScreenListener.onSuckToLeftBottom(v);
                }
            } else if (isInRight && isInTop && isEnd && isTop) {
                if (mFloatButtonSuckScreenListener != null) {
                    mFloatButtonSuckScreenListener.onSuckToRightTop(v);
                }
            } else if (isInRight && isInBottom && isEnd && isBottom) {
                if (mFloatButtonSuckScreenListener != null) {
                    mFloatButtonSuckScreenListener.onSuckToRightBottom(v);
                }
            } else if (isInLeft && isStart) {
                if (mFloatButtonSuckScreenListener != null) {
                    mFloatButtonSuckScreenListener.onSuckToStart(v);
                }
            } else if (isInRight && isEnd) {
                if (mFloatButtonSuckScreenListener != null) {
                    mFloatButtonSuckScreenListener.onSuckToEnd(v);
                }
            } else if (isInTop && isTop) {
                if (mFloatButtonSuckScreenListener != null) {
                    mFloatButtonSuckScreenListener.onSuckToTop(v);
                }
            } else if (isInBottom && isBottom) {
                if (mFloatButtonSuckScreenListener != null) {
                    mFloatButtonSuckScreenListener.onSuckToBottom(v);
                }
            }
        }
    }


    /**
     * 悬浮按钮的监听器
     */
    public interface FloatButtonLayoutListener {
        /**
         * 按钮被单击时
         *
         * @param v 按钮本身
         */
        void onClick(View v);

        /**
         * 按钮在移动的时候
         *
         * @param v       按钮本身
         * @param offsetX 按钮的水平偏移量
         * @param offsetY 按钮的垂直偏移量
         */
        void onMove(View v, float offsetX, float offsetY);

        /**
         * 非单击松开手指时
         *
         * @param v 按钮本身
         */
        void onFingerUp(View v);

    }

    /**
     * 悬浮按钮吸屏监听器
     */
    public interface FloatButtonSuckScreenListener {

        /**
         * 吸附到屏幕左边的时候
         *
         * @param v
         */
        void onSuckToStart(View v);

        /**
         * 吸附到屏幕右边的时候
         *
         * @param v
         */
        void onSuckToEnd(View v);

        /**
         * 吸附到屏幕顶部的时候
         *
         * @param v
         */
        void onSuckToTop(View v);

        /**
         * 吸附到屏幕底部的时候
         *
         * @param v
         */
        void onSuckToBottom(View v);

        /**
         * 吸附到屏幕左上角的时候
         *
         * @param v
         */
        void onSuckToLeftTop(View v);

        /**
         * 吸附到屏幕左下角的时候
         *
         * @param v
         */
        void onSuckToLeftBottom(View v);

        /**
         * 吸附到屏幕右上角的时候
         *
         * @param v
         */
        void onSuckToRightTop(View v);

        /**
         * 吸附到屏幕右下角的时候
         *
         * @param v
         */
        void onSuckToRightBottom(View v);
    }

    /**
     * 悬浮按钮的吸屏方向
     */
    public static class SuckScreenDirection {
        /**
         * 无吸屏效果(默认)
         */
        public static final int NONE = 0;
        /**
         * 当按钮的中心点在屏幕宽度的中心靠左的时候，左部吸屏
         */
        public static final int START = 0x00001;
        /**
         * 当按钮的中心点在屏幕宽度的中心靠右的时候，右部吸屏
         */
        public static final int END = 0x00010;
        /**
         * 当按钮的中心点在屏幕高度的中心靠顶部的时候，顶部部吸屏
         */
        public static final int TOP = 0x00100;
        /**
         * 当按钮的中心点在屏幕高度的中心靠底部的时候，底部部吸屏
         */
        public static final int BOTTOM = 0x01000;
        /**
         * 综合了左上右下的所有吸屏，实际效果就是吸附在屏幕四个角
         */
        public static final int ALL = 0x10000;
    }
}
