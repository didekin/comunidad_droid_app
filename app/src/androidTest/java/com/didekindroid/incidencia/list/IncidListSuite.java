package com.didekindroid.incidencia.list;

import com.didekindroid.incidencia.core.IncidEditAcNoPowerTest_3;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 13/02/17
 * Time: 13:22
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ControllerIncidCloseSeeTest.class,
        ControllerIncidOpenSeeTest.class,
        IncidSeeClosedByComuAc_Mn_Test.class,
        IncidSeeClosedByComuAcTest.class,
        IncidSeeOpenByComuAc_GCM_Test.class,
        IncidSeeOpenByComuAcTest_1.class,
        IncidSeeOpenByComuAcTest_2.class,
        IncidSeeOpenByComuAcTest_3.class,
        IncidSeeOpenByComuAcTest_4.class,
        IncidEditAcNoPowerTest_3.class,
        ReactorIncidCloseSeeByComuTest.class,
        ReactorIncidOpenSeeByComuTest.class,
})
public class IncidListSuite {
}
