package com.didekindroid.usuariocomunidad;

import com.didekindroid.usuariocomunidad.dao.UserComuDaoRemoteTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 22/11/16
 * Time: 10:41
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
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
        UsuarioComunidadBeanValidaTests.class,
})
public class UserComuSuite {
}
