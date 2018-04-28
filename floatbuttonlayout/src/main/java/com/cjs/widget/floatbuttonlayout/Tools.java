package com.cjs.widget.floatbuttonlayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * 描述:FloatButtonLayout辅助工具类
 * <p>
 * 作者:陈俊森
 * 创建时间:2018年04月26日 16:01
 * 邮箱:chenjunsen@outlook.com
 *
 * @version 1.0
 */
public class Tools {
    public static final int ERROR_CALL_BACK = -1;

    /**
     * 获取状态栏高度<br/>
     * <p>
     * 此方法通过获取系统自身的资源进行反射得到状态栏高度，也就是说不管你的页面有没有设置显示状态栏，都会返回状态栏高度<br>
     * 如果想获取页面实时的状态栏高度，请使用{@link #getScreenStatusBarHeight(Activity)}
     * <p/>
     *
     * @param context
     * @return 返回像素单位的状态栏高度，如果获取失败，返回{@link #ERROR_CALL_BACK}
     */
    public static int getSystemStatusBarHeight(Context context) {
        int statusBarSrcId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarSrcId > 0) {
            return context.getResources().getDimensionPixelOffset(statusBarSrcId);
        } else {
            return ERROR_CALL_BACK;
        }
    }

    /**
     * <h1>获取状态栏高度<h1/>
     * <p>
     * 此方法的调用请在{@link Activity#onWindowFocusChanged(boolean)}中使用，否则返回零。<br>
     * 此方法会实时获取页面的状态栏高度，如果有状态栏就会返回真实的状态栏值，否则返回零
     * </p>
     *
     * @param activity
     * @return 返回像素单位的状态栏高度
     */
    public static int getScreenStatusBarHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top < 0 ? 0 : rect.top;
    }

    /**
     * 获取屏幕宽度(像素)
     *
     * @param context
     * @return
     */
    public static int getDeviceScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度(像素)
     *
     * @param context
     * @return
     */
    public static int getDeviceScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取底部导航栏的高度
     * @param context
     * @return
     */
    public static int getBottomNavigationBarHeight(Context context) {
        int resourceId = 0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
/*            Log.d("fbl", "高度：" + resourceId);
            Log.d("fbl", "高度：" + context.getResources().getDimensionPixelSize(resourceId) + "");*/
            return context.getResources().getDimensionPixelSize(resourceId);
        } else return 0;
    }

    /**
     * 判断当前页面是否全屏
     * @param activity
     * @return
     */
    public static boolean isFullScreen(Activity activity) {
        boolean res = (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        Log.d("tools","是否全屏:"+res);
        return res;
    }

    /**
     * 判断底部导航栏是否存在(该方法在三星s8+手动开启隐藏导航栏时无效，永远为true)
     * @param activity
     * @return
     */
    public static boolean isBottomNavigationBarExists(Activity activity){
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }
}
