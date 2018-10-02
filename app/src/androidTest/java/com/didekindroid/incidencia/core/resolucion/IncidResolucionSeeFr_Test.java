package com.didekindroid.incidencia.core.resolucion;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityNextMock;
import com.didekindroid.lib_one.api.router.FragmentInitiator;
import com.didekindroid.lib_one.usuario.UserTestData;
import com.didekinlib.model.incidencia.dominio.Avance;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.core.resolucion.IncidResolucionSeeFr.newInstance;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataResolucionSeeFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkResolucionSeeViews;
import static com.didekindroid.incidencia.testutils.IncidTestData.AVANCE_DEFAULT_DES;
import static com.didekindroid.incidencia.testutils.IncidTestData.COSTE_ESTIM_DEFAULT;
import static com.didekindroid.incidencia.testutils.IncidTestData.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidTestData.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidTestData.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidTestData.doResolucion;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_COMMENTS_SEE_AC;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.util.UiUtil.formatTimeStampToString;
import static com.didekindroid.lib_one.util.UiUtil.getMilliSecondsFromCalendarAdd;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static java.util.Calendar.SECOND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class IncidResolucionSeeFr_Test {

    @Rule
    public ActivityTestRule<ActivityNextMock> activityRule = new ActivityTestRule<>(ActivityNextMock.class, false, true);

    private AppCompatActivity activity;
    private IncidResolucionSeeFr fr;

    private FragmentInitiator<IncidResolucionSeeFr> initiator;
    private final Incidencia incidencia = doIncidencia(UserTestData.user_crodrigo.getUserName(), INCID_DEFAULT_DESC, 999L, 111L, (short) 2);
    private final Resolucion resolucion = doResolucion(
            incidencia,
            RESOLUCION_DEFAULT_DESC,
            COSTE_ESTIM_DEFAULT,
            new Timestamp(getMilliSecondsFromCalendarAdd(SECOND, 100)));

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        initiator = new FragmentInitiator<>(activity, com.didekindroid.lib_one.R.id.next_mock_ac_layout);
    }

    @Test
    public void test_Miscelanea()
    {
        initFragment();
        // test_newInstance
        assertThat(fr.getArguments().getSerializable(INCIDENCIA_OBJECT.key), is(incidencia));
        assertThat(fr.getArguments().getSerializable(INCID_RESOLUCION_OBJECT.key), is(resolucion));
        // test_OnActivityCreate.
        assertThat(fr.resolucion.equals(resolucion), is(true));
        // test_PaintViewData.
        checkResolucionSeeViews();
        checkDataResolucionSeeFr(resolucion);
        // Avances.
        waitAtMost(4, SECONDS).until(() -> {
            onView(allOf(
                    withId(android.R.id.empty),
                    withText(R.string.incid_resolucion_no_avances_message))
            ).check(matches(isDisplayed()));
            return true;
        });
    }

    @Test
    public void test_PaintViewData_avances()
    {
        // Modificamos con avances.
        Avance avance = new Avance.AvanceBuilder()
                .avanceDesc(AVANCE_DEFAULT_DES)
                .author(USER_PEPE)
                .fechaAlta(new Timestamp(getMilliSecondsFromCalendarAdd(SECOND, 100)))
                .build();
        List<Avance> avances = new ArrayList<>(1);
        avances.add(avance);
        Resolucion resolucionAdv = new Resolucion.ResolucionBuilder(incidencia)
                .copyResolucion(resolucion)
                .avances(avances)
                .build();
        initFragment(incidencia, resolucionAdv);
        // test_PaintViewData.
        checkResolucionSeeViews();
        checkDataResolucionSeeFr(resolucionAdv);
        // Avances.
        onData(is(avance)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));
        onView(allOf(
                withText(avance.getAvanceDesc()),
                withId(R.id.incid_avance_desc_view)
        )).check(matches(isDisplayed()));
        onView(allOf(
                withText(formatTimeStampToString(avance.getFechaAlta())),
                withId(R.id.incid_avance_fecha_view),
                hasSibling(allOf(
                        withId(R.id.incid_avance_aliasUser_view),
                        withText(avance.getAuthor().getAlias()) // usuario en sesión que modifica resolución.
                )))).check(matches(isDisplayed()));
    }

    @Test
    public void test_OnOptionsItemSelected() throws Exception
    {
        IncidImportancia incidImportancia = insertGetIncidImportancia(COMU_ESCORIAL_PEPE);
        Resolucion resolucionDb = new Resolucion.ResolucionBuilder(incidImportancia.getIncidencia())
                .copyResolucion(resolucion)
                .build();
        initFragment(incidImportancia.getIncidencia(), resolucionDb);
        INCID_COMMENTS_SEE_AC.checkItem(activity);

        cleanOptions(CLEAN_PEPE);
    }

    /*    ============================  Helpers  ===================================*/

    private void initFragment()
    {
        initFragment(incidencia, resolucion);
    }

    private void initFragment(Incidencia incidencia, Resolucion resolucion)
    {
        initiator.initFragmentTx(newInstance(incidencia, resolucion));
        waitAtMost(4, SECONDS).until(() -> (fr = (IncidResolucionSeeFr) activity.getSupportFragmentManager().findFragmentByTag(IncidResolucionSeeFr.class.getName())) != null);
    }
}