package com.didekindroid.utils;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.utils.IoHelperTest;

import org.junit.runner.RunWith;

import static com.didekinaar.AppInitializer.creator;

/**
 * User: pedro@didekin
 * Date: 01/01/17
 * Time: 17:43
 */
@RunWith(AndroidJUnit4.class)
public final class IoHelperApp_test extends IoHelperTest {

    @Override
    protected Context getContext()
    {
        return creator.get().getContext();
    }
}
