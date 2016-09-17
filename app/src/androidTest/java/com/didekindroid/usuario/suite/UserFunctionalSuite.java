package com.didekindroid.usuario.suite;

import com.didekindroid.usuario.activity.ComuDataAcTest;
import com.didekindroid.usuario.activity.ComuSearchAc_1_Test;
import com.didekindroid.usuario.activity.ComuSearchAc_2_Test;
import com.didekindroid.usuario.activity.ComuSearchResultsAc_1_Test;
import com.didekindroid.usuario.activity.DeleteMeAcTest;
import com.didekindroid.usuario.activity.LoginAc_1_Test;
import com.didekindroid.usuario.activity.PasswordChangeAcTest;
import com.didekindroid.usuario.activity.RegComuAndUserAndUserComuAcTest;
import com.didekindroid.usuario.activity.RegComuAndUserComuAcTest;
import com.didekindroid.usuario.activity.RegUserAndUserComuAcTest;
import com.didekindroid.usuario.activity.RegUserComuAcTest;
import com.didekindroid.usuario.activity.SeeUserComuByComuAc_1_Test;
import com.didekindroid.usuario.activity.SeeUserComuByComuAc_2_Test;
import com.didekindroid.usuario.activity.SeeUserComuByUserAcTest;
import com.didekindroid.usuario.activity.UserComuDataAc_1_Test;
import com.didekindroid.usuario.activity.UserComuDataAc_2_Test;
import com.didekindroid.usuario.activity.UserDataAcTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 09:49
 */
@SuppressWarnings("EmptyClass")
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ComuDataAcTest.class,
        ComuSearchAc_1_Test.class,
        ComuSearchAc_2_Test.class,
        ComuSearchResultsAc_1_Test.class,
        DeleteMeAcTest.class,
        LoginAc_1_Test.class,
        PasswordChangeAcTest.class,
        RegComuAndUserAndUserComuAcTest.class,
        RegComuAndUserComuAcTest.class,
        RegUserAndUserComuAcTest.class,
        RegUserComuAcTest.class,
        SeeUserComuByComuAc_1_Test.class,
        SeeUserComuByComuAc_2_Test.class,
        SeeUserComuByUserAcTest.class,
        UserComuDataAc_1_Test.class,
        UserComuDataAc_2_Test.class,
        UserDataAcTest.class
})
public class UserFunctionalSuite {
}
