package com.cjs.widget.floatbuttonlayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

/**
 * 描述:悬浮拖拽按钮
 * <p>
 * 作者:陈俊森
 * 创建时间:2018年05月04日 9:33
 * 邮箱:chenjunsen@outlook.com
 *
 * @version 1.0
 */
@SuppressLint("AppCompatCustomView")
public class FloatButton extends ImageButton implements ViewTreeObserver.OnGlobalLayoutListener {
    static final String TAG = "FB";
    static final String TAG_TOUCH = "FB_TOUCH";
    /**
     * 日志开关
     */
    private boolean openLog = true;
    /**
     * 日志管理器
     */
    private L log;
    /**
     * 当前的按钮是否处于被拖拽的状态
     */
    private boolean isInDrag;
    /**
     * 上一次的触摸点横坐标（相对于整个手机屏幕）
     */
    private float lastRawX;
    /**
     * 上一次的触摸点纵坐标（相对于整个手机屏幕）
     */
    private float lastRawY;
    /**
     * 上一次静止时的触摸点横坐标（相对于整个手机屏幕），该值与{@link #lastRawX}的区别是，它是在一次触摸操作完全完成时才会变化，
     * 而后者会在触摸过程中不断发生变化
     */
    private float lastStandRawX;
    /**
     * 上一次静止时的触摸点纵坐标（相对于整个手机屏幕），该值与{@link #lastRawY}的区别是，它是在一次触摸操作完全完成时才会变化，
     * 而后者会在触摸过程中不断发生变化
     */
    private float lastStandRawY;
    /**
     * 判断是否是拖拽的最小偏移量(像素)
     */
    private int mMinDragOffset;
    /**
     * 是否开启吸屏效果
     */
    private boolean isEnableStickScreen;
    /**
     * 是否允许按钮可以超出父布局移动
     */
    private boolean isAllowMoveBeyondParentLayout;

    /**
     * 允许吸屏的方向
     */
    private int mStickDirection;
    /**
     * 是否允许吸屏的方向
     */
    private boolean isStickStart, isStickEnd, isStickTop, isStickBottom, isStickCenterVertical, isStickCenterHorizontal;
    private boolean isStickTopStart, isStickTopEnd, isStickBottomStart, isStickBottomEnd;
    private boolean isStickCenterInParent, isStickCenterStart, isStickCenterEnd, isStickCenterTop, isStickCenterBottom;
    /**
     * 当前按钮所在父布局的宽高
     */
    private int mParentWidth, mParentHeight;
    /**
     * 是否开启吸屏动画效果
     */
    private boolean isEnableStickAnimation;

    public FloatButton(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public FloatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public FloatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public FloatButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        log = new L(openLog);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FloatButton);
        mMinDragOffset = array.getDimensionPixelOffset(R.styleable.FloatButton_min_drag_offset, 2);
        isEnableStickScreen = array.getBoolean(R.styleable.FloatButton_enable_stick_screen, true);
        isEnableStickAnimation = array.getBoolean(R.styleable.FloatButton_allow_stick_animation, true);
        isAllowMoveBeyondParentLayout = array.getBoolean(R.styleable.FloatButton_allow_move_beyond_parent_layout, false);
        mStickDirection = array.getInteger(R.styleable.FloatButton_stick_direction, StickyDirection.START | StickyDirection.END);

        //计算吸屏方向
        int maskDirection = StickyDirection.MASK & mStickDirection;
        isStickStart = (maskDirection & StickyDirection.START) == StickyDirection.START;
        isStickEnd = (maskDirection & StickyDirection.END) == StickyDirection.END;
        isStickTop = (maskDirection & StickyDirection.TOP) == StickyDirection.TOP;
        isStickBottom = (maskDirection & StickyDirection.BOTTOM) == StickyDirection.BOTTOM;
        isStickCenterHorizontal = (maskDirection & StickyDirection.CENTER_HORIZONTAL) == StickyDirection.CENTER_HORIZONTAL;
        isStickCenterVertical = (maskDirection & StickyDirection.CENTER_VERTICAL) == StickyDirection.CENTER_VERTICAL;

        log.d(TAG, "isStickStart:" + isStickStart);
        log.d(TAG, "isStickEnd:" + isStickEnd);
        log.d(TAG, "isStickTop:" + isStickTop);
        log.d(TAG, "isStickBottom:" + isStickBottom);
        log.d(TAG, "isStickCenterHorizontal:" + isStickCenterHorizontal);
        log.d(TAG, "isStickCenterVertical:" + isStickCenterVertical);

        getViewTreeObserver().addOnGlobalLayoutListener(this);
        array.recycle();
    }

    private void initDimension() {
        mParentWidth = ((View) getParent()).getMeasuredWidth();
        mParentHeight = ((View) getParent()).getMeasuredHeight();
        log.d(TAG, "parentWidth:" + mParentWidth);
        log.d(TAG, "parentHeight:" + mParentHeight);
        log.d(TAG, "widgetWidth:" + getMeasuredWidth());
        log.d(TAG, "widgetHeight:" + getMeasuredHeight());
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        //当前触摸点相对屏幕坐标
        float rawX = event.getRawX();
        float rawY = event.getRawY();

        //当前控件中心点坐标
        float widgetCenterX = getX() + getMeasuredWidth() / 2;
        float widgetCenterY = getY() + getMeasuredHeight() / 2;

        //按钮最终停留位置
        float x = getX();
        float y = getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isInDrag = false;
                log.e(TAG_TOUCH, "ACTION_DOWN");
                log.d(TAG_TOUCH, "ACTION_DOWN x:" + x + " y:" + y);
                getParent().requestDisallowInterceptTouchEvent(true);
                lastRawX = rawX;
                lastRawY = rawY;
                lastStandRawX = rawX;
                lastStandRawY = rawY;
                break;
            case MotionEvent.ACTION_UP:
                log.e(TAG_TOUCH, "ACTION_UP");
                if (isInDrag) {
                    setPressed(false);
                } else {
                    log.e(TAG_TOUCH, "click.....");
                }
                if (isEnableStickScreen) {
                    log.e(TAG_TOUCH, "吸屏功能已经开启");
                    float sensitiveHPart = 0;//水平方向吸屏敏感区域间距
                    if (isStickCenterHorizontal) {
                        sensitiveHPart = (float) mParentWidth / 3;
                        if (widgetCenterX < sensitiveHPart) {//靠左
                            log.d(TAG_TOUCH, "到达水平靠左区域");
                            if (isStickStart) {
                                x = 0;
                            } else {
                                log.w(TAG_TOUCH, "但是靠左区域吸屏功能没有开启，不执行吸屏");
                            }
                        } else if (sensitiveHPart <= widgetCenterX && widgetCenterX <= sensitiveHPart * 2) {//靠中心
                            log.d(TAG_TOUCH, "到达水平居中区域");
                            x = sensitiveHPart + getMeasuredWidth() / 2;
                        } else {//靠右
                            log.d(TAG_TOUCH, "到达水平居右区域");
                            if (isStickEnd) {
                                x = mParentWidth - getMeasuredWidth();
                            } else {
                                log.w(TAG_TOUCH, "但是靠右区域吸屏功能没有开启，不执行吸屏");
                            }
                        }
                    } else {
                        sensitiveHPart = mParentWidth / 2;
                        if (widgetCenterX < sensitiveHPart) {//靠左
                            log.d(TAG_TOUCH, "到达靠左区域");
                            if (isStickStart) {
                                x = 0;
                            } else {
                                log.w(TAG_TOUCH, "但是靠左区域吸屏功能没有开启，不执行吸屏");
                            }
                        } else {//靠右
                            log.d(TAG_TOUCH, "到达靠右区域");
                            if (isStickEnd) {
                                x = mParentWidth - getMeasuredWidth();
                            } else {
                                log.w(TAG_TOUCH, "但是靠右区域吸屏功能没有开启，不执行吸屏");
                            }
                        }
                    }
                    float sensitiveVPart = 0;//垂直方向吸屏敏感区域间距
                    if (isStickCenterVertical) {
                        sensitiveVPart = (float) mParentHeight / 3;
                        if (widgetCenterY < sensitiveVPart) {//靠顶部w
                            log.d(TAG_TOUCH, "到达垂直靠顶区域");
                            if (isStickTop) {
                                y = 0;
                            } else {
                                log.w(TAG_TOUCH, "但是靠顶区域吸屏没有开启，不执行吸屏");
                            }
                        } else if (sensitiveVPart <= widgetCenterY && widgetCenterY <= sensitiveVPart * 2) {//靠中心
                            log.d(TAG_TOUCH, "到达垂直居中区域");
                            y = sensitiveVPart + getMeasuredHeight() / 2;
                        } else {//靠底部
                            log.d(TAG_TOUCH, "到达垂直靠底区域");
                            if (isStickBottom) {
                                y = mParentHeight - getMeasuredHeight();
                            } else {
                                log.w(TAG_TOUCH, "但是靠底区域吸屏没有开启，不执行吸屏");
                            }
                        }
                    } else {
                        sensitiveVPart = (float) mParentHeight / 2;
                        if (widgetCenterY < sensitiveVPart) {//靠顶部
                            log.d(TAG_TOUCH, "到达靠顶区域");
                            if (isStickTop) {
                                y = 0;
                            } else {
                                log.w(TAG_TOUCH, "但是靠顶区域吸屏没有开启，不执行吸屏");
                            }
                        } else {//靠底部
                            log.d(TAG_TOUCH, "到达靠底区域");
                            if (isStickBottom) {
                                y = mParentHeight - getMeasuredHeight();
                            } else {
                                log.w(TAG_TOUCH, "但是靠底区域吸屏没有开启，不执行吸屏");
                            }
                        }
                    }
                    log.d(TAG_TOUCH, "sensitiveHPart:" + sensitiveHPart);
                    log.d(TAG_TOUCH, "sensitiveVPart:" + sensitiveVPart);
                    handleStickPosition(x, y);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                log.e(TAG_TOUCH, "ACTION_MOVE");
                float dRawX = rawX - lastRawX;
                float dRawY = rawY - lastRawY;
                float dStandRawX = rawX - lastStandRawX;
                float dStandRawY = rawY - lastStandRawY;
                isInDrag = isDrag(dRawX, dRawY, dStandRawX, dStandRawY);
                if (isInDrag) {
                    log.d(TAG_TOUCH, "moving.....");
                    x = getX() + dRawX;
                    y = getY() + dRawY;
                    if (!isAllowMoveBeyondParentLayout) {
                        if (x >= 0) {
                            float maxX = mParentWidth - getMeasuredWidth();
                            x = Math.min(x, maxX);
                        } else {
                            x = 0;
                        }
                        if (y >= 0) {
                            float maxY = mParentHeight - getMeasuredHeight();
                            y = Math.min(y, maxY);
                        } else {
                            y = 0;
                        }
                    }
                    setX(x);
                    setY(y);
                    lastRawX = rawX;
                    lastRawY = rawY;
                }
                break;
        }
        return isInDrag || super.onTouchEvent(event);
    }

    private void handleStickPosition(float x, float y) {
        log.d(TAG, "handleStickPosition->x:" + x + " y:" + y);
        if (isEnableStickAnimation) {
            log.d(TAG, "stick动画开启");
            ObjectAnimator oaX = ObjectAnimator.ofFloat(this, "x", getX(), x);
            ObjectAnimator oaY = ObjectAnimator.ofFloat(this, "y", getY(), y);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(120);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.playTogether(oaX, oaY);
            animatorSet.start();
        } else {
            setX(x);
            setY(y);
        }
    }

    /**
     * 判断是否是拖拽
     *
     * @param offsetX      水平偏移量
     * @param offsetY      垂直偏移量
     * @param standOffsetX 相对于上一次完全操作成功后的静止水平偏移量
     * @param standOffsetY 相对于上一次完全操作成功后的静止垂直偏移量
     * @return
     */
    private boolean isDrag(float offsetX, float offsetY, float standOffsetX, float standOffsetY) {
        return Math.sqrt(offsetX * offsetX + offsetY * offsetY) >= mMinDragOffset || Math.sqrt(standOffsetX * standOffsetX + standOffsetY * standOffsetY) >= mMinDragOffset;
    }

    /**
     * 在这里监听父布局控件宽高改变的状态，适用于三星S8,S8+系列手机可以随时开启关闭底部虚拟导航栏的情况
     */
    @Override
    public void onGlobalLayout() {
        log.d(TAG, "------------->onGlobalLayout<-----------------");
        initDimension();
    }

    @Override
    protected void onDetachedFromWindow() {
        log.d(TAG, "------------->onDetachedFromWindow<-----------------");
        super.onDetachedFromWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }

    /**
     * 描述:悬浮按钮吸屏方向
     * <p>
     * <br>作者: 陈俊森
     * <br>创建时间: 2018/5/5 0005 10:06
     * <br>邮箱: chenjunsen@outlook.com
     *
     * @version 1.0
     */
    public static class StickyDirection {
        /**
         * 吸屏到顶部
         */
        public static final int TOP = 0x000001;
        /**
         * 吸屏到底部
         */
        public static final int BOTTOM = 0x000010;
        /**
         * 吸屏到左侧
         */
        public static final int START = 0x000100;
        /**
         * 吸屏到右侧
         */
        public static final int END = 0x001000;
        /**
         * 吸屏到垂直中部
         */
        public static final int CENTER_VERTICAL = 0x010000;
        /**
         * 吸屏到水平中部
         */
        public static final int CENTER_HORIZONTAL = 0x100000;

        /************************************后续开发中************************************/
        /**
         * 吸屏到右侧居中
         */
        public static final int CENTER_END = 1 << 6;
        /**
         * 吸屏到左侧居中
         */
        public static final int CENTER_START = 1 << 7;
        /**
         * 吸屏到顶部居中
         */
        public static final int CENTER_TOP = 1 << 8;
        /**
         * 吸屏到底部居中
         */
        public static final int CENTER_BOTTOM = 1 << 9;
        /**
         * 吸屏到父布局中心
         */
        public static final int CENTER_IN_PARENT = 11;
        /**
         * 吸屏到左上角
         */
        public static final int TOP_START = 12;
        /**
         * 吸屏到右上角
         */
        public static final int TOP_END = 13;
        /**
         * 吸屏到左下角
         */
        public static final int BOTTOM_START = 14;
        /**
         * 吸屏到右下角
         */
        public static final int BOTTOM_END = 15;

        /**
         * 掩码
         */
        static final int MASK = TOP | BOTTOM | START | END
                | CENTER_VERTICAL | CENTER_HORIZONTAL;
//                | CENTER_END | CENTER_START | CENTER_TOP
//                | CENTER_BOTTOM | CENTER_IN_PARENT | TOP_START | TOP_END | BOTTOM_START | BOTTOM_END;
    }
}
