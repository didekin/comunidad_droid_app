package com.didekindroid.incidencia.core.reg;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.usuariocomunidad.spinner.ComuSpinnerEventItemSelect;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.isComuSpinnerWithText;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidRegFrLayout;
import static com.didekindroid.lib_one.incidencia.spinner.IncidenciaSpinnerKey.AMBITO_INCIDENCIA_POSITION;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.regTwoUserComuSameUser;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 31/03/17
 * Time: 15:25
 */
@RunWith(AndroidJUnit4.class)
public class ViewerIncidRegFrTest {

    private static final String AMBITO_SPINNER_INIT_VALUE = "ámbito de incidencia";

    private ViewerIncidRegFr viewer;
    private IncidRegAc activity;
    private static UsuarioComunidad userComuIntent;
    private View frgView;

    @Rule
    public IntentsTestRule<IncidRegAc> activityRule = new IntentsTestRule<IncidRegAc>(IncidRegAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            return new Intent().putExtra(COMUNIDAD_ID.key, userComuIntent.getComunidad().getC_Id());
        }
    };

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        regTwoUserComuSameUser(asList(COMU_ESCORIAL_PEPE, COMU_LA_FUENTE_PEPE));
        userComuIntent = userComuDao.seeUserComusByUser().blockingGet().get(1);
    }

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        frgView = activity.findViewById(incidRegFrLayout);
        assertThat(activity.getIntent().getLongExtra(COMUNIDAD_ID.key, 0), is(userComuIntent.getComunidad().getC_Id()));
        waitAtMost(4, SECONDS).until(() -> activity.incidRegFr != null && activity.incidRegFr.viewer != null);
        viewer = activity.incidRegFr.viewer;
    }

    @AfterClass
    public static void clearUp()
    {
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void testDoViewInViewer()
    {
        assertThat(viewer.getController(), notNullValue());
        assertThat(viewer.getParentViewer(), notNullValue());
        assertThat(viewer.viewerAmbitoIncidSpinner, notNullValue());
        assertThat(viewer.viewerComuSpinner, notNullValue());
        assertThat(viewer.viewerImportanciaSpinner, notNullValue());

        onView(withId(incidRegFrLayout)).check(matches(isDisplayed()));

        // Comunidad spinner.
        waitAtMost(6, SECONDS).until(isComuSpinnerWithText(userComuIntent.getComunidad().getNombreComunidad()));
        // Ámbito incidencia spinner.
        onView(allOf(withId(R.id.app_spinner_1_dropdown_item), withParent(withId(R.id.incid_reg_ambito_spinner))))
                .check(matches(withText(is(AMBITO_SPINNER_INIT_VALUE)))).check(matches(isDisplayed()));
        // Importancia spinner.
        onView(withId(R.id.incid_reg_importancia_spinner))
                .check(matches(withSpinnerText(activity.getResources().getStringArray(R.array.IncidImportanciaArray)[0])))
                .check(matches(isDisplayed()));

        // testSaveState
        Bundle bundleTest = new Bundle();
        viewer.viewerAmbitoIncidSpinner.setSelectedItemId(11);
        viewer.viewerImportanciaSpinner.setSelectedItemId((short) 31);
        // Solo hay una comunidad en el spinner.
        viewer.saveState(bundleTest);
        assertThat(bundleTest.getLong(AMBITO_INCIDENCIA_POSITION.key), is(11L));
        assertThat(bundleTest.getLong(INCID_IMPORTANCIA_NUMBER.key), is(31L));
        assertThat(bundleTest.containsKey(COMUNIDAD_ID.key), is(true));

        // test_DoOnClickItemId
        viewer.doOnClickItemId(new ComuSpinnerEventItemSelect());
        assertThat(viewer.atomIncidBean.get().getComunidadId(), is(0L));

        viewer.doOnClickItemId(new ComuSpinnerEventItemSelect(new Comunidad.ComunidadBuilder().c_id(23L).build()));
        assertThat(viewer.atomIncidBean.get().getComunidadId(), is(23L));

        // testClearSubscriptions
        checkSubscriptionsOnStop(activity, viewer.getController(),
                viewer.viewerAmbitoIncidSpinner.getController(),
                viewer.viewerComuSpinner.getController());
    }

    @Test
    public void testDoIncidImportanciaFromView()
    {
        final AtomicBoolean isRun = new AtomicBoolean(false);

        // Preconditions:
        activity.runOnUiThread(() -> {
            viewer.doViewInViewer(new Bundle(), null);
            isRun.compareAndSet(false, true);
        });
        waitAtMost(4, SECONDS).untilTrue(isRun);

        // Data for test OK.
        viewer.atomIncidBean.get().setComunidadId(2L);
        viewer.atomIncidBean.get().setCodAmbitoIncid((short) 29);
        viewer.atomIncidImportBean.get().setImportancia((short) 1);
        isRun.set(false);
        activity.runOnUiThread(() -> {
            EditText editText = frgView.findViewById(R.id.incid_reg_desc_ed);
            editText.setText("Descripción válida");
            isRun.compareAndSet(false, true);
        });
        waitAtMost(1, SECONDS).untilTrue(isRun);

        // Errors container.
        StringBuilder errors = getErrorMsgBuilder(getTargetContext());
        // Exec and checkMenu for OK case.
        assertThat(viewer.doIncidImportanciaFromView(errors).getImportancia(), is((short) 1));

        // Data for test NOT ok.
        viewer.atomIncidImportBean.get().setImportancia((short) 111);
        // Exec and checkMenu for NOT ok case.
        assertThat(viewer.doIncidImportanciaFromView(errors), nullValue());
        assertThat(errors.toString(), containsString(activity.getResources().getText(R.string.incid_reg_importancia).toString()));
    }
}