package com.didekindroid.usuario;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.InjectorOfParentViewerIf;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.usuario.RegUserFr;
import com.didekindroid.lib_one.usuario.ViewerRegUserFr;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.ViewerRegComuUserUserComuAc;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.typeUserNameAlias;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/05/17
 * Time: 11:00
 */
@RunWith(AndroidJUnit4.class)
public class ViewerRegUserFrTest {

    @Rule
    public ActivityTestRule<RegComuAndUserAndUserComuAc> activityRule = new ActivityTestRule<>(RegComuAndUserAndUserComuAc.class, true, true);
    RegUserFr fragment;
    RegComuAndUserAndUserComuAc activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        fragment = (RegUserFr) activity.getSupportFragmentManager().findFragmentById(R.id.reg_user_frg);

        AtomicReference<ViewerRegUserFr> viewerAtomic = new AtomicReference<>(null);
        viewerAtomic.compareAndSet(null, fragment.getViewer());
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
    }

    @Test
    public void test_OnActivityCreated() throws Exception
    {
        assertThat(fragment.getViewer().getController(), nullValue());
        assertThat(ViewerRegComuUserUserComuAc.class.isInstance(fragment.getViewer().getParentViewer()), is(true));
        assertThat(InjectorOfParentViewerIf.class.isInstance(activity), is(true));
        ParentViewerIf parentViewer = (ParentViewerIf) fragment.getViewer().getParentViewer();
        assertThat(parentViewer.getChildViewer(ViewerRegUserFr.class), is(fragment.getViewer()));
    }

    @Test
    public void test_GetUserFromViewerOk() throws Exception
    {
        typeUserNameAlias("yo@email.com", "alias1");
        assertThat(fragment.getViewer().getUserFromViewer(new StringBuilder()), allOf(
                notNullValue(),
                is(new Usuario.UsuarioBuilder().userName("yo@email.com").alias("alias1").password("password1").build())
        ));
    }

    @Test
    public void test_GetUserFromViewerWrong() throws Exception
    {
        typeUserNameAlias("yo_email.com", "alias1");
        assertThat(fragment.getViewer().getUserFromViewer(new StringBuilder()), nullValue());
    }
}