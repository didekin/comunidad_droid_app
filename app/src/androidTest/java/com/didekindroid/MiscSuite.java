package com.didekindroid;

import com.didekindroid.accesorio.ConfidencialidadAcTest;
import com.didekindroid.api.ApiSuite;
import com.didekindroid.exception.UiExceptionTest;
import com.didekindroid.router.ActivityInitiatorTest;
import com.didekindroid.router.ActivityRouterTest;
import com.didekindroid.router.FragmentInitiatorTest;
import com.didekindroid.router.ViewerDrawerMain_NotReg_Test;
import com.didekindroid.router.ViewerDrawerMain_Reg_Test;
import com.didekindroid.util.DeviceTest;
import com.didekindroid.util.IoHelperTest;
import com.didekindroid.util.UIutilsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 12:47
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // Accesorio.
        ConfidencialidadAcTest.class,
        // api.
        ApiSuite.class,
        // exception
        UiExceptionTest.class,
        // router.
        ActivityInitiatorTest.class,
        ActivityRouterTest.class,
        FragmentInitiatorTest.class,
        ViewerDrawerMain_NotReg_Test.class,
        ViewerDrawerMain_Reg_Test.class,
        // utils
        DeviceTest.class,
        IoHelperTest.class,
        UIutilsTest.class,
})
public class MiscSuite {
}
