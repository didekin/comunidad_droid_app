package com.didekindroid.usuario;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectedIf;
import com.didekindroid.api.ViewerParentInjectorIf;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.ViewerRegComuUserUserComuAc;
import com.didekinlib.model.usuario.Usuario;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserDataFull;
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
        viewerAtomic.compareAndSet(null, fragment.viewer);
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
    }

    @Test
    public void test_OnActivityCreated() throws Exception
    {
        assertThat(fragment.viewer.getController(), nullValue());
        assertThat(ViewerRegComuUserUserComuAc.class.isInstance(fragment.viewer.getParentViewer()), is(true));
        assertThat(ViewerParentInjectorIf.class.isInstance(activity), is(true));
        ViewerParentInjectedIf parentViewer = (ViewerParentInjectedIf) fragment.viewer.getParentViewer();
        assertThat(parentViewer.getChildViewer(ViewerRegUserFr.class), CoreMatchers.<ViewerIf>is(fragment.viewer));
    }

    @Test
    public void test_GetUserFromViewerOk() throws Exception
    {
        typeUserDataFull("yo@email.com", "alias1", "password1", "password1");
        assertThat(fragment.viewer.getUserFromViewer(new StringBuilder()), allOf(
                notNullValue(),
                is(new Usuario.UsuarioBuilder().userName("yo@email.com").alias("alias1").password("password1").build())
        ));
    }

    @Test
    public void test_GetUserFromViewerWrong() throws Exception
    {
        typeUserDataFull("yo_email.com", "alias1", "password1", "password1");
        assertThat(fragment.viewer.getUserFromViewer(new StringBuilder()), nullValue());
    }
}