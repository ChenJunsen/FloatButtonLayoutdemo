package com.cjs.widget.demo.floatbuttonlayoutdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cjs.widget.floatbuttonlayout.FloatButtonLayout;

/**
 * 描述:悬浮按钮布局demo
 *
 * <br>作者: 陈俊森
 * <br>创建时间: 2018/4/26 0026 21:14
 * <br>邮箱: chenjunsen@outlook.com
 * @version 1.0
 */
public class MainActivity extends Activity {
    FloatButtonLayout mFloatButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_button_layout);
        mFloatButtonLayout=findViewById(R.id.fbl);
        mFloatButtonLayout.setSuckScreenDirection(FloatButtonLayout.SuckScreenDirection.ALL);
        mFloatButtonLayout.setAllowMoveBeyondScreen(false);
        mFloatButtonLayout.setFloatButtonSuckScreenListener(new FloatButtonLayout.FloatButtonSuckScreenListener() {
            @Override
            public void onSuckToStart(View v) {
                showToast("start");
            }

            @Override
            public void onSuckToEnd(View v) {
                showToast("end");
            }

            @Override
            public void onSuckToTop(View v) {
                showToast("top");
            }

            @Override
            public void onSuckToBottom(View v) {
                showToast("bottom");
            }

            @Override
            public void onSuckToLeftTop(View v) {
                showToast("leftTop");
            }

            @Override
            public void onSuckToLeftBottom(View v) {
                showToast("leftBottom");
            }

            @Override
            public void onSuckToRightTop(View v) {
                showToast("rightTop");
            }

            @Override
            public void onSuckToRightBottom(View v) {
                showToast("rightBottom");
            }
        });
        mFloatButtonLayout.setFloatButtonLayoutListener(new FloatButtonLayout.FloatButtonLayoutListener() {
            @Override
            public void onClick(View v) {
                showToast("点我干嘛");
            }

            @Override
            public void onMove(View v, float offsetX, float offsetY) {
                Log.d("fbl","offsetX:"+offsetX+" offsetY:"+offsetY);
            }

            @Override
            public void onFingerUp(View v) {
                Log.d("fbl","手指移开");
            }
        });

        findViewById(R.id.float_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(i);
            }
        });

    }

    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
