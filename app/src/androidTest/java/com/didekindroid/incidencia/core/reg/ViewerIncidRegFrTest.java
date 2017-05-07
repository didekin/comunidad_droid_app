package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.core.reg.ViewerIncidRegFr.newViewerIncidRegFr;
import static com.didekindroid.incidencia.utils.IncidBundleKey.AMBITO_INCIDENCIA_POSITION;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static com.didekindroid.testutil.ActivityTestUtils.addSubscription;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.isComuSpinnerWithText;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 31/03/17
 * Time: 15:25
 */
@RunWith(AndroidJUnit4.class)
public class ViewerIncidRegFrTest {

    public static final String AMBITO_SPINNER_INIT_VALUE = "ámbito de incidencia";

    ViewerIncidRegFr viewer;
    IncidRegAc activity;
    UsuarioComunidad pepeUserComu;

    @Rule
    public ActivityTestRule<IncidRegAc> activityRule = new ActivityTestRule<IncidRegAc>(IncidRegAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                pepeUserComu = userComuDaoRemote.seeUserComusByUser().get(0);
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    int fragmentLayoutId = R.id.incid_reg_frg;
    View frgView;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        frgView = activity.findViewById(R.id.incid_reg_frg);
        viewer = newViewerIncidRegFr(frgView, new ViewerIncidRegAc(activity));
    }

    @After
    public void clearUp() throws UiException
    {
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void testNewViewerIncidReg() throws Exception
    {
        assertThat(viewer.getController(), nullValue());
        assertThat(viewer.getParentViewer(), notNullValue());
        assertThat(viewer.viewerAmbitoIncidSpinner, notNullValue());
        assertThat(viewer.viewerComuSpinner, notNullValue());
        assertThat(viewer.viewerImportanciaSpinner, notNullValue());
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        onView(withId(fragmentLayoutId)).check(matches(isDisplayed()));

        // Comunidad spinner.
        waitAtMost(4, SECONDS).until(isComuSpinnerWithText(pepeUserComu.getComunidad().getNombreComunidad()));
        // Ámbito incidencia spinner.
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.incid_reg_ambito_spinner))))
                .check(matches(withText(is(AMBITO_SPINNER_INIT_VALUE)))).check(matches(isDisplayed()));
        // Importancia spinner.
        onView(withId(R.id.incid_reg_importancia_spinner))
                .check(matches(withSpinnerText(activity.getResources().getStringArray(R.array.IncidImportanciaArray)[0])))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testClearSubscriptions() throws Exception
    {
        addSubscription(viewer.viewerComuSpinner.getController());
        addSubscription(viewer.viewerAmbitoIncidSpinner.getController());
        addSubscription(viewer.viewerImportanciaSpinner.getController());

        assertThat(viewer.clearSubscriptions(), is(0));
        assertThat(viewer.viewerComuSpinner.getController().getSubscriptions().size(), is(0));
        assertThat(viewer.viewerAmbitoIncidSpinner.getController().getSubscriptions().size(), is(0));
        assertThat(viewer.viewerImportanciaSpinner.getController().getSubscriptions().size(), is(0));
    }

    @Test
    public void testSaveState() throws Exception
    {
        Bundle bundleTest = new Bundle();
        viewer.viewerAmbitoIncidSpinner.setItemSelectedId(11);
        viewer.viewerImportanciaSpinner.setItemSelectedId((short) 31);
        // Solo hay una comunidad en el spinner.
        viewer.saveState(bundleTest);

        assertThat(bundleTest.getLong(AMBITO_INCIDENCIA_POSITION.key), is(11L));
        assertThat(bundleTest.getLong(INCID_IMPORTANCIA_NUMBER.key), is(31L));
        assertThat(bundleTest.containsKey(COMUNIDAD_ID.key), is(true));
    }

    @Test
    public void testDoIncidImportanciaFromView() throws Exception
    {
        final AtomicBoolean isRun = new AtomicBoolean(false);

        // Preconditions:
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.doViewInViewer(new Bundle(), null);
                isRun.compareAndSet(false,true);
            }
        });
        waitAtMost(1,SECONDS).untilTrue(isRun);

        StringBuilder errors = getErrorMsgBuilder(getTargetContext());
        viewer.atomIncidBean.get().setComunidadId(2L);
        viewer.atomIncidBean.get().setCodAmbitoIncid((short) 29);

        isRun.set(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                EditText editText = (EditText) frgView.findViewById(R.id.incid_reg_desc_ed);
                editText.setText("Descripción válida");
                isRun.compareAndSet(false,true);
            }
        });
        waitAtMost(1,SECONDS).untilTrue(isRun);

        viewer.atomIncidImportBean.get().setImportancia((short) 1);
        assertThat(viewer.doIncidImportanciaFromView(errors), notNullValue());
        assertThat(viewer.doIncidImportanciaFromView(errors).getImportancia(), is((short) 1));

        viewer.atomIncidImportBean.get().setImportancia((short) 111);
        assertThat(viewer.doIncidImportanciaFromView(errors), nullValue());
        assertThat(errors.toString(), containsString(activity.getResources().getText(R.string.incid_reg_importancia).toString()));
    }

    @Test
    public void testDoOnClickItemId() throws Exception
    {
        assertThat(viewer.doOnClickItemId(123L, null), is(123L));
    }
}