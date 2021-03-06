package com.didekindroid.usuario;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.usuario.UserDataAc;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetUser;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.lib_one.usuario.ViewerUserDataIf.UserChangeToMake.alias_only;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 25/03/17
 * Time: 14:16
 */
@RunWith(AndroidJUnit4.class)
public class ViewerUserData_App_Test {

    private Usuario usuario;

    @Rule
    public ActivityTestRule<UserDataAc> activityRule = new ActivityTestRule<UserDataAc>(UserDataAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                usuario = regComuUserUserComuGetUser(comu_real_rodrigo);
            } catch (Exception e) {
                fail();
            }
            return new Intent().putExtra(user_name.key, usuario.getUserName());
        }
    };

    private UserDataAc activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        waitAtMost(4, SECONDS).until(() -> activity.getViewer() != null);
    }

    @After
    public void cleanUp()
    {
        cleanOneUser(user_crodrigo.getUserName());
    }

    // ============================================================
    //    .................... TESTS ....................
    // ============================================================

    @Test
    public void testModifyUserData()
    {
        // Datos de entrada userName == oldUser.userName.
        waitAtMost(8, SECONDS).untilAtomic(activity.getViewer().getOldUser(), is(usuario));
        activity.getViewer().getOldUser().set(new Usuario.UsuarioBuilder().copyUsuario(usuario).password(user_crodrigo.getPassword()).build());
        activity.getViewer().getNewUser().set(new Usuario.UsuarioBuilder().copyUsuario(activity.getViewer().getOldUser().get()).build());
        activity.runOnUiThread(() -> activity.getViewer().modifyUserData(alias_only));
        waitAtMost(8, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
    }
}