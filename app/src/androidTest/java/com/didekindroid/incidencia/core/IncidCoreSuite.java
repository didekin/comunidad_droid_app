package com.didekindroid.incidencia.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 13/02/17
 * Time: 13:22
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IncidCloseAc_GCM_Test.class,
        IncidEditAcMaxPowerTest_1.class,
        IncidEditAcMaxPowerTest_2.class,
        IncidEditAcMaxPowerTest_3.class,
        IncidEditAcNoPowerTest_1.class,
        IncidEditAcNoPowerTest_2.class,
        IncidEditAcNoPowerTest_3.class,
        IncidEditAcTest_Mn1.class,
        IncidEditAcTest_Mn2.class,
        IncidEditAcTest_Mn3.class,
        IncidEditAcTest_Mn4.class,
        IncidenciaBeanTest.class,
        IncidenciaDataDbHelperTest.class,
        IncidRegAc_GCM_Test.class,
        IncidRegAcTest_1.class,
})
public class IncidCoreSuite {
}
