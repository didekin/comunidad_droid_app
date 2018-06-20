package com.didekindroid.incidencia.core.resolucion;

import android.support.test.rule.ActivityTestRule;
import android.support.v7.app.AppCompatActivity;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.router.FragmentInitiator;
import com.didekindroid.lib_one.usuario.UserTestData;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static com.didekindroid.incidencia.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.core.resolucion.IncidResolucionSeeFr.newInstance;
import static com.didekindroid.incidencia.testutils.IncidTestData.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidTestData.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidTestData.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidTestData.doResolucion;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("ConstantConditions")
public class IncidResolucionSeeFr_Test {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, false, true);

    private AppCompatActivity activity;
    private FragmentInitiator<IncidResolucionSeeFr> initiator;
    private final Incidencia incidencia = doIncidencia(UserTestData.user_crodrigo.getUserName(), INCID_DEFAULT_DESC, 999L, (short) 2);
    private final Resolucion resolucion = doResolucion(
            incidencia,
            RESOLUCION_DEFAULT_DESC,
            1000,
            new Timestamp(Instant.now().plus(100, SECONDS).toEpochMilli())
    );

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        initiator = new FragmentInitiator<>(activity, com.didekindroid.lib_one.R.id.mock_ac_layout);
    }

    @Test
    public void test_NewInstance()
    {
        initiator.initFragmentTx(newInstance(incidencia, resolucion));
        IncidResolucionSeeFr fr = (IncidResolucionSeeFr) activity.getSupportFragmentManager().findFragmentByTag(IncidResolucionSeeFr.class.getName());
        assertThat(fr.getArguments().getSerializable(INCIDENCIA_OBJECT.key), is(incidencia));
        assertThat(fr.getArguments().getSerializable(INCID_RESOLUCION_OBJECT.key), is(resolucion));
    }

    @Test
    public void test_OnActivityCreated()
    {
        // TODO: seguir.
    }

    @Test
    public void test_OnOptionsItemSelected()
    {
    }

    @Test
    public void test_PaintViewData()
    {
    }
}