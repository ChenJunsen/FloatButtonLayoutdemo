package com.cjs.widget.demo.floatbuttonlayoutdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cjs.widget.floatbuttonlayout.FloatButton;

public class Main2Activity extends Activity {
    public static final int ACTION_1 = 1;
    public static final int ACTION_2 = 2;
    public static final int ACTION_3 = 3;
    private FloatButton mFloatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getIntent().getExtras();
        int layoutId = data.getInt("ACTION");
        switch (layoutId) {
            case ACTION_1:
                setContentView(R.layout.activity_float_button_single_stick);
                break;
            case ACTION_2:
                setContentView(R.layout.activity_float_button_left_right_stick);
                break;
            case ACTION_3:
                setContentView(R.layout.activity_float_button_no_stick);
                break;
        }

        mFloatButton = findViewById(R.id.float_button);
        mFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Main2Activity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
