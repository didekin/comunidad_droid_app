package com.didekindroid.utils;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 01/11/2017
 * Time: 14:15
 */
@RunWith(AndroidJUnit4.class)
public class DeviceUtilTest {

    @Test
    public void test_GetAppLanguage() throws Exception
    {
        Timber.d("===================== App language: %s ==============================", DeviceUtil.getAppLanguage());
    }

    @Test
    public void test_GetDeviceLanguage() throws Exception
    {
        Timber.d("==================== Device language: %s ============================", DeviceUtil.getDeviceLanguage());
    }
}