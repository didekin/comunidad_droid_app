package com.didekindroid.lib_one.util;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.util.Device.getDeviceLanguage;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

/**
 * User: pedro@didekin
 * Date: 10/11/2017
 * Time: 13:20
 */
@RunWith(AndroidJUnit4.class)
public class DeviceTest {

    @Test
    public void test_GetDeviceLanguage() throws Exception
    {
        assertThat(getDeviceLanguage(), allOf(
                isA(String.class),
                notNullValue()
        ));
    }

}