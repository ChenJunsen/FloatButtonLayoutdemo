package com.cjs.widget.demo.floatbuttonlayoutdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cjs.widget.floatbuttonlayout.FloatButton;

public class Main2Activity extends Activity {
    private FloatButton mFloatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mFloatButton=findViewById(R.id.float_button);
        mFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Main2Activity.this,"OK",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
