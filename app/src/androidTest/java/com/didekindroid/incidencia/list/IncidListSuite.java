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
        // list.close
        CtrlerIncidSeeCloseByComuTest.class,
        ViewerIncidSeeCloseFrTest.class,
        IncidSeeByComuAc_Mn_Test.class,
        IncidSeeByComuAcTest.class,
        // list.open
        CtrlerIncidSeeOpenByComuTest.class,
        ViewerIncidSeeOpenFrTest.class,
        IncidSeeOpenByComuAc_GCM_Test.class,
        IncidSeeOpenByComuAc_Mn_Test.class,
        IncidSeeOpenByComuAcTest.class,
})
public class IncidListSuite {
}
