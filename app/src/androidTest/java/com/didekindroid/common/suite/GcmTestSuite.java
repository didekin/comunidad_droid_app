package com.didekindroid.common.suite;

import com.didekindroid.common.gcm.GcmBroadCastNotificationActivityTest;
import com.didekindroid.common.gcm.GcmNotificationTest;
import com.didekindroid.common.gcm.GcmRequestTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 01/06/16
 * Time: 14:06
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        GcmBroadCastNotificationActivityTest.class,
        GcmRequestTest.class,
        GcmNotificationTest.class,
})
public class GcmTestSuite {
}
