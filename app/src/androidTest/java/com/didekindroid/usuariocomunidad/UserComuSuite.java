package com.didekindroid.usuariocomunidad;

import com.didekindroid.usuariocomunidad.dao.UserComuDaoRemoteTest;
import com.didekindroid.usuariocomunidad.dao.UserComuObservableTest;
import com.didekindroid.usuariocomunidad.listbycomu.CtrlerUserComuByComuTest;
import com.didekindroid.usuariocomunidad.listbycomu.SeeUserComuByComuAc_1_Test;
import com.didekindroid.usuariocomunidad.listbycomu.SeeUserComuByComuAc_2_Test;
import com.didekindroid.usuariocomunidad.listbycomu.ViewerSeeUserComuByComuTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 22/11/16
 * Time: 10:41
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CtrlerUserComuByComuTest.class,
        RegComuAndUserAndUserComuAcTest.class,
        RegComuAndUserComuAcTest.class,
        RegUserAndUserComuAcTest.class,
        RegUserComuAcTest.class,
        SeeUserComuByComuAc_1_Test.class,
        SeeUserComuByComuAc_2_Test.class,
        SeeUserComuByUserAcTest.class,
        UserComuDataAc_1_Test.class,
        UserComuDataAc_2_Test.class,
        UserComuDaoRemoteTest.class,
        UserComuObservableTest.class,
        UsuarioComunidadBeanValidaTests.class,
        ViewerSeeUserComuByComuTest.class,
})
public class UserComuSuite {
}
