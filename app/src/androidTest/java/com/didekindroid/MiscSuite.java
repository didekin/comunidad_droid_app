package com.didekindroid;

import com.didekindroid.accesorio.ConfidencialidadAcTest;
import com.didekindroid.lib_one.LibOneSuite;
import com.didekindroid.router.UiExceptionActionTest;
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
        LibOneSuite.class,
        // exception
        UiExceptionActionTest.class,
        // router.
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
