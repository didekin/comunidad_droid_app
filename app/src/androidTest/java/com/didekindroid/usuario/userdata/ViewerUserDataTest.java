package com.didekindroid.usuario.userdata;

import android.support.test.rule.ActivityTestRule;

import com.didekindroid.R;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserData;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.userdata.ViewerUserData.newViewerUserData;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.alias_only;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.nothing;
import static com.didekindroid.usuario.userdata.ViewerUserDataIf.UserChangeToMake.userName;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 25/03/17
 * Time: 14:16
 */
public class ViewerUserDataTest {

    @Rule
    public ActivityTestRule<UserDataAc> activityRule = new ActivityTestRule<>(UserDataAc.class, true, true);

    UserDataAc activity;
    ViewerUserData viewer;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        viewer = (ViewerUserData) newViewerUserData(activity);
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {

    }

    @Test
    public void testProcessBackUserDataLoaded() throws Exception
    {
         // TODO: lo que testamos en la activity es el cambio en intentForMenu y que una vez
         // invalidado el menú, se carga en el intent en la activity. Esto es lo que hay que testar,
        // quizás comprobando el intent en los tests de llamadas a los menús.
    }

    @Test
    public void testGetIntentForMenu() throws Exception
    {

    }

    @Test
    public void testCheckUserData_1() throws Exception
    {
        typeUserData("newuser@user.com", USER_JUAN.getAlias(), USER_JUAN.getPassword());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(viewer.checkUserData(), is(true));
            }
        });
        assertThat(viewer.usuarioBean.get().getUserName(), is("newuser@user.com"));
        assertThat(viewer.usuarioBean.get().getAlias(), is(USER_JUAN.getAlias()));
        assertThat(viewer.usuarioBean.get().getPassword(), is(USER_JUAN.getPassword()));
    }

    @Test
    public void testCheckUserData_2()
    {
        typeUserData("wrong_newuser.com", USER_JUAN.getAlias(), USER_JUAN.getPassword());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(viewer.checkUserData(), is(false));
            }
        });
        waitAtMost(1, SECONDS).until(isToastInView(R.string.email_hint, activity));
    }

    @Test
    public void testWhatDataChangeToMake() throws Exception
    {
        viewer.oldUser.set(new Usuario.UsuarioBuilder().alias("alias1").userName("name1").build());
        viewer.newUser.set(new Usuario.UsuarioBuilder().alias("alias1").userName("name1").build());
        assertThat(viewer.whatDataChangeToMake(), is(nothing));
        viewer.newUser.set(new Usuario.UsuarioBuilder().alias("alias2").userName("name1").build());
        assertThat(viewer.whatDataChangeToMake(), is(alias_only));
        viewer.newUser.set(new Usuario.UsuarioBuilder().alias("alias1").userName("name2").build());
        assertThat(viewer.whatDataChangeToMake(), is(userName));
        viewer.newUser.set(new Usuario.UsuarioBuilder().alias("alias2").userName("name2").build());
        assertThat(viewer.whatDataChangeToMake(), is(userName));
    }

    public void testModifyUserData() throws Exception
    {
       // Tested in the activity test.
    }

    @Test
    public void testProcessControllerError() throws Exception
    {
        // TODO: no vale la implementación en Viewer.
    }

}