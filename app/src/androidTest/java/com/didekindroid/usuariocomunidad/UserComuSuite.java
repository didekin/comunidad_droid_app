package com.didekindroid.usuariocomunidad;

import com.didekindroid.usuariocomunidad.dao.UserComuDaoRemoteTest;
import com.didekindroid.usuariocomunidad.dao.UserComuObservableTest;
import com.didekindroid.usuariocomunidad.data.UserComuDataAc_1_Test;
import com.didekindroid.usuariocomunidad.data.UserComuDataAc_2_Test;
import com.didekindroid.usuariocomunidad.listbycomu.CtrlerUserComuByComuTest;
import com.didekindroid.usuariocomunidad.listbycomu.SeeUserComuByComuAcTest;
import com.didekindroid.usuariocomunidad.listbycomu.ViewerSeeUserComuByComuTest;
import com.didekindroid.usuariocomunidad.listbyuser.SeeUserComuByUserAcTest;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAcTest;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserComuAcTest;
import com.didekindroid.usuariocomunidad.register.RegUserAndUserComuAcTest;
import com.didekindroid.usuariocomunidad.register.RegUserComuAcTest;
import com.didekindroid.usuariocomunidad.spinner.CtrlerComuSpinnerTest;
import com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinnerTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 22/11/16
 * Time: 10:41
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CtrlerComuSpinnerTest.class,
        CtrlerUserComuByComuTest.class,
        RegComuAndUserAndUserComuAcTest.class,
        RegComuAndUserComuAcTest.class,
        RegUserAndUserComuAcTest.class,
        RegUserComuAcTest.class,
        SeeUserComuByComuAcTest.class,
        SeeUserComuByUserAcTest.class,
        UserComuDataAc_1_Test.class,
        UserComuDataAc_2_Test.class,
        UserComuDaoRemoteTest.class,
        UserComuObservableTest.class,
        UsuarioComunidadBeanTests.class,
        ViewerComuSpinnerTest.class,
        ViewerSeeUserComuByComuTest.class,
})
public class UserComuSuite {
}
