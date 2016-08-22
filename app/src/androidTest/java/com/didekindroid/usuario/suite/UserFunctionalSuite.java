package com.didekindroid.usuario.suite;

import com.didekindroid.usuario.activity.ComuDataAcTest;
import com.didekindroid.usuario.activity.ComuSearchAcTest_1;
import com.didekindroid.usuario.activity.ComuSearchAcTest_2;
import com.didekindroid.usuario.activity.ComuSearchAcTest_intent;
import com.didekindroid.usuario.activity.ComuSearchAcTest_spinner;
import com.didekindroid.usuario.activity.ComuSearchResultsAcTest_1;
import com.didekindroid.usuario.activity.ComuSearchResultsAcTest_intent_1;
import com.didekindroid.usuario.activity.ComuSearchResultsAcTest_intent_2;
import com.didekindroid.usuario.activity.DeleteMeAcTest;
import com.didekindroid.usuario.activity.LoginAcTest_1;
import com.didekindroid.usuario.activity.PasswordChangeAcTest;
import com.didekindroid.usuario.activity.RegComuAndUserAndUserComuAcTest_1;
import com.didekindroid.usuario.activity.RegComuAndUserAndUserComuAcTest_2;
import com.didekindroid.usuario.activity.RegComuAndUserAndUserComuAcTest_3;
import com.didekindroid.usuario.activity.RegComuAndUserAndUserComuAcTest_4;
import com.didekindroid.usuario.activity.RegComuAndUserAndUserComuAcTest_5;
import com.didekindroid.usuario.activity.RegComuAndUserComuAcTest;
import com.didekindroid.usuario.activity.RegUserAndUserComuAcTest_intent;
import com.didekindroid.usuario.activity.RegUserComuAcTest;
import com.didekindroid.usuario.activity.RegUserComuAcTest_intent;
import com.didekindroid.usuario.activity.SeeUserComuByComuAcTest;
import com.didekindroid.usuario.activity.SeeUserComuByUserAcTest;
import com.didekindroid.usuario.activity.UserComuDataAcTest_1;
import com.didekindroid.usuario.activity.UserComuDataAcTest_2;
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
        ComuSearchAcTest_1.class,
        ComuSearchAcTest_2.class,
        ComuSearchAcTest_intent.class,
        ComuSearchAcTest_spinner.class,
        ComuSearchResultsAcTest_1.class,
        ComuSearchResultsAcTest_intent_1.class,
        ComuSearchResultsAcTest_intent_2.class,
        DeleteMeAcTest.class,
        LoginAcTest_1.class,
        PasswordChangeAcTest.class,
        RegComuAndUserAndUserComuAcTest_1.class,
        RegComuAndUserAndUserComuAcTest_2.class,
        RegComuAndUserAndUserComuAcTest_3.class,
        RegComuAndUserAndUserComuAcTest_4.class,
        RegComuAndUserAndUserComuAcTest_5.class,
        RegComuAndUserComuAcTest.class,
        RegUserAndUserComuAcTest_intent.class,
        RegUserComuAcTest.class,
        RegUserComuAcTest_intent.class,
        SeeUserComuByComuAcTest.class,
        SeeUserComuByUserAcTest.class,
        UserComuDataAcTest_1.class,
        UserComuDataAcTest_2.class,
        UserDataAcTest.class
})
public class UserFunctionalSuite {
}
