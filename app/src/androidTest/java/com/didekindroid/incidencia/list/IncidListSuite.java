package com.didekindroid.incidencia.list;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 13/02/17
 * Time: 13:22
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CtrlerIncidSeeCloseByComuTest.class,
        CtrlerIncidSeeOpenByComuTest.class,
        IncidSeeByComuAc_Close_Mn_Test.class,
        IncidSeeByComuAc_Close_Test.class,
        IncidSeeByComuAc_Open_GCM_Test.class,
        IncidSeeByComuAc_Open_Mn_Test.class,
        IncidSeeByComuAc_Open_Test.class,
        ViewerIncidSeeCloseFrTest.class,
        ViewerIncidSeeOpenFrTest.class,
})
public class IncidListSuite {
}
