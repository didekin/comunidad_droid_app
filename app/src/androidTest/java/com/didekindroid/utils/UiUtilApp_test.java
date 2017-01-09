package com.didekindroid.utils;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.utils.UIutilsTest;

import org.junit.runner.RunWith;

import static com.didekinaar.AppInitializer.creator;

/**
 * User: pedro@didekin
 * Date: 01/01/17
 * Time: 17:46
 */
@RunWith(AndroidJUnit4.class)
public final class UiUtilApp_test extends UIutilsTest {

    @Override
    protected Context getContext()
    {
        return creator.get().getContext();
    }
}
