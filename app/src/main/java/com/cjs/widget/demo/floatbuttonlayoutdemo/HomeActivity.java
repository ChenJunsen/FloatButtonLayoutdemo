package com.cjs.widget.demo.floatbuttonlayoutdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity implements View.OnClickListener {
    private Button btn_float_button_layout, btn_1, btn_2, btn_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btn_float_button_layout = findViewById(R.id.btn_float_button_layout);
        btn_1 = findViewById(R.id.btn_float_button_1);
        btn_2 = findViewById(R.id.btn_float_button_2);
        btn_3 = findViewById(R.id.btn_float_button_3);

        btn_3.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_float_button_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int action=0;
        Class clazz=Main2Activity.class;
        if (v == btn_1) {
            action=Main2Activity.ACTION_1;
        } else if (v == btn_2) {
            action=Main2Activity.ACTION_2;
        } else if (v == btn_3) {
            action=Main2Activity.ACTION_3;
        } else if (v == btn_float_button_layout) {
            clazz=MainActivity.class;
        }
        Bundle b=new Bundle();
        b.putInt("ACTION",action);
        start2Activity(clazz, b);
    }

    public void start2Activity(Class<? extends Activity> clazz, Bundle data) {
        Intent i = new Intent(this, clazz);
        if (data != null) {
            i.putExtras(data);
        }
        startActivity(i);
    }
}
