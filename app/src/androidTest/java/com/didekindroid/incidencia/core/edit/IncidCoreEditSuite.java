package com.didekindroid.incidencia.core.edit;

import com.didekindroid.incidencia.core.reg.CtrlerIncidRegEditFr_Reg_Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 07/04/17
 * Time: 17:56
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // Activities
        IncidEditAcNoPowerTest_1.class,
        IncidEditAcNoPowerTest_2.class,
        IncidEditAcNoPowerTest_3.class,
        IncidEditAcTest_Mn1.class,
        IncidEditAcTest_Mn2.class,
        IncidEditAcTest_Mn3.class,
        IncidEditAcTest_Mn4.class,
})
public class IncidCoreEditSuite {
}
