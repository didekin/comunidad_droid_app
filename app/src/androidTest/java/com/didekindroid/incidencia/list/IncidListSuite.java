package com.didekindroid.incidencia.list;

import com.didekindroid.incidencia.list.close.CtrlerIncidSeeCloseByComuTest;
import com.didekindroid.incidencia.list.close.IncidSeeClosedByComuAcTest;
import com.didekindroid.incidencia.list.close.IncidSeeClosedByComuAc_Mn_Test;
import com.didekindroid.incidencia.list.close.ViewerIncidSeeCloseTest;
import com.didekindroid.incidencia.list.open.CtrlerIncidSeeOpenByComuTest;
import com.didekindroid.incidencia.list.open.IncidSeeOpenByComuAcTest_1;
import com.didekindroid.incidencia.list.open.IncidSeeOpenByComuAcTest_2;
import com.didekindroid.incidencia.list.open.IncidSeeOpenByComuAcTest_3;
import com.didekindroid.incidencia.list.open.IncidSeeOpenByComuAc_GCM_Test;
import com.didekindroid.incidencia.list.open.IncidSeeOpenByComuAc_Mn_Test;
import com.didekindroid.incidencia.list.open.ViewerIncidSeeOpenTest;

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
        IncidSeeClosedByComuAc_Mn_Test.class,
        IncidSeeClosedByComuAcTest.class,
        IncidSeeOpenByComuAc_GCM_Test.class,
        IncidSeeOpenByComuAc_Mn_Test.class,
        IncidSeeOpenByComuAcTest_1.class,
        IncidSeeOpenByComuAcTest_2.class,
        IncidSeeOpenByComuAcTest_3.class,
        ViewerIncidListByComuTest.class,
        ViewerIncidSeeCloseTest.class,
        ViewerIncidSeeOpenTest.class,
})
public class IncidListSuite {
}
