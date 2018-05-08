package com.cjs.widget.floatbuttonlayout;

import android.util.Log;

/**
 * 描述:
 * <p>
 * 作者:陈俊森
 * 创建时间:2018年05月05日 9:41
 * 邮箱:chenjunsen@outlook.com
 *
 * @version 1.0
 */
public class L {
    private boolean isLog;

    public L(boolean isLog) {
        this.isLog = isLog;
    }

    public void d(String tag, String msg){
        if(isLog)
        Log.d(tag,msg);
    }
    public void e(String tag,String msg){
        if(isLog)
            Log.e(tag,msg);
    }
    public void w(String tag,String msg){
        if(isLog){
            Log.w(tag, msg);
        }
    }
}
