package com.kevin.commonwidgetlib;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kevin.commonwidget.CircleWaveView;

/**
 * Created by Kevin on 2021/12/3<br/>
 * <p>
 * Describe:<br/>
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircleWaveView cwv = findViewById(R.id.cwv);
    }
}
