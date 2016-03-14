package com.didekindroid.incidencia.suite;

import com.didekindroid.incidencia.activity.IncidCommentRegAcTest;
import com.didekindroid.incidencia.activity.IncidCommentSeeAcTest_1;
import com.didekindroid.incidencia.activity.IncidCommentSeeAcTest_2;
import com.didekindroid.incidencia.activity.IncidEditAcTest_1;
import com.didekindroid.incidencia.activity.IncidEditAcTest_2;
import com.didekindroid.incidencia.activity.IncidEditAcTest_3;
import com.didekindroid.incidencia.activity.IncidEditAcTest_Mn1;
import com.didekindroid.incidencia.activity.IncidEditAcTest_Mn2;
import com.didekindroid.incidencia.activity.IncidEditAcTest_Mn3;
import com.didekindroid.incidencia.activity.IncidEditAcTest_Mn4;
import com.didekindroid.incidencia.activity.IncidRegAcTest;
import com.didekindroid.incidencia.activity.IncidResolucionRegAcTest;
import com.didekindroid.incidencia.activity.IncidResolucionSeeDefaultAcTest;
import com.didekindroid.incidencia.activity.IncidSeeByComuAcTest_1;
import com.didekindroid.incidencia.activity.IncidSeeByComuAcTest_2;
import com.didekindroid.incidencia.activity.IncidSeeByComuAcTest_3;
import com.didekindroid.incidencia.activity.IncidSeeClosedByComuAcTest_1;
import com.didekindroid.incidencia.gcm.IncidRegAcTest_gcm1;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 18:55
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IncidCommentRegAcTest.class,
        IncidCommentSeeAcTest_1.class,
        IncidCommentSeeAcTest_2.class,
        IncidEditAcTest_1.class,
        IncidEditAcTest_2.class,
        IncidEditAcTest_3.class,
        IncidEditAcTest_Mn1.class,
        IncidEditAcTest_Mn2.class,
        IncidEditAcTest_Mn3.class,
        IncidEditAcTest_Mn4.class,
        IncidRegAcTest.class,
        IncidRegAcTest_gcm1.class,
        IncidResolucionRegAcTest.class,
        IncidResolucionSeeDefaultAcTest.class,
        IncidSeeByComuAcTest_1.class,
        IncidSeeByComuAcTest_2.class,
        IncidSeeByComuAcTest_3.class,
        IncidSeeClosedByComuAcTest_1.class,
})
public class IncidFunctionalTestSuite {
}
