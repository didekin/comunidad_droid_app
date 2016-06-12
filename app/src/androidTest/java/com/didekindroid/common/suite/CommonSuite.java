package com.didekindroid.common.suite;

import com.didekindroid.common.activity.TokenHandlerTest;
import com.didekindroid.common.activity.UiExceptionTests_1;
import com.didekindroid.common.activity.UiExceptionTests_2;
import com.didekindroid.common.gcm.GcmBroadCastNotificationActivityTest;
import com.didekindroid.common.gcm.GcmRegistrationIntentService_Test;
import com.didekindroid.common.gcm.GcmRequestTest;
import com.didekindroid.common.utils.BundleKeyTest;
import com.didekindroid.common.utils.IoHelperTest;
import com.didekindroid.common.utils.UIutilsTest;
import com.didekindroid.common.webservices.JceTests;
import com.didekindroid.common.webservices.Oauth2ServiceIfTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 09:43
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        BundleKeyTest.class,
        GcmBroadCastNotificationActivityTest.class,
        GcmRequestTest.class,
        GcmRegistrationIntentService_Test.class,
        IoHelperTest.class,
        JceTests.class,
        Oauth2ServiceIfTest.class,
        TokenHandlerTest.class,
        UiExceptionTests_1.class,
        UiExceptionTests_2.class,
        UIutilsTest.class,
})
public class CommonSuite {
}
