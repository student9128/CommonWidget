package com.kevin.commonwidgetlib;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.runner.RunWith;

/**
 * Created by Kevin on 2021/12/4<br/>
 * <p>
 * Describe:<br/>
 */
@RunWith(AndroidJUnit4.class)
public class SwitchActivityTest {
    public void setUp() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(),TestActivity.class );

    }
}
