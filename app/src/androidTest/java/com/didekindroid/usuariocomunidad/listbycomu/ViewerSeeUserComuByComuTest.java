package com.didekindroid.usuariocomunidad.listbycomu;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.didekindroid.R;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_LA_PLAZUELA_5;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN2;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayedAndPerform;
import static com.didekindroid.usuariocomunidad.RolUi.ADM;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuByComuRol;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuNoPortalNoEscalera;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuPlantaNoPuerta;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuPlantaPuerta;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuPortalEscalera;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuPortalNoEscalera;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.runFinalCheckUserComuByComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_PEPE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/03/17
 * Time: 14:54
 */
@RunWith(AndroidJUnit4.class)
public class ViewerSeeUserComuByComuTest {

    private UsuarioComunidad usuarioComunidad;
    @Rule
    public IntentsTestRule<SeeUserComuByComuAc> activityRule = new IntentsTestRule<SeeUserComuByComuAc>(SeeUserComuByComuAc.class, true) {

        @Override
        protected Intent getActivityIntent()
        {
            regUserComuWithTkCache(COMU_REAL_PEPE);
            usuarioComunidad = userComuDao.seeUserComusByUser().blockingGet().get(0);
            return new Intent().putExtra(COMUNIDAD_ID.key, usuarioComunidad.getComunidad().getC_Id());
        }
    };
    private SeeUserComuByComuAc activity;
    private SeeUserComuByComuFr fragment;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        waitAtMost(4, SECONDS).until(
                () -> {
                    fragment = (SeeUserComuByComuFr) activity.getSupportFragmentManager().findFragmentById(R.id.see_usercomu_by_comu_frg);
                    return fragment.viewer;
                },
                notNullValue()
        );
        // Esperamos hasta que se ha hecho la 'primera' carga de datos.
        checkAdapterAndHeader(1);
    }

    @After
    public void cleanUp()
    {
        cleanOptions(CLEAN_PEPE);
    }

    // ==================================  TESTS  =================================

    @Test
    public void test_NewViewerUserComuByComu()
    {
        assertThat(fragment.viewer.getViewInViewer(), instanceOf(ListView.class));
        assertThat(fragment.viewer.nombreComuView, notNullValue());
        assertThat(fragment.viewer.getViewInViewer().getHeaderViewsCount(), is(1));
        assertThat(fragment.viewer.getController(), instanceOf(CtrlerUserComuByComuList.class));
    }

    @Test
    public void test_OnSuccessLoadItems_1()
    {
        final List<UsuarioComunidad> list = new ArrayList<>(1);
        list.add(usuarioComunidad);
        // Run and check.
        runAndCheckAdapterAndHeader(list, 1);
    }

    /* Test de presentación de los datos en la pantalla.*/
    @Test
    public void test_OnSuccessLoadItems_2()
    {
        UsuarioComunidad usuarioComunidad1 = new UsuarioComunidad.UserComuBuilder(
                COMU_LA_PLAZUELA_5,
                USER_JUAN)
                .portal("portalW")
                .planta("PL")
                .escalera("ESC_2")
                .puerta("123")
                .roles(PRO.function)
                .build();
        // No escalera, no puerta.
        UsuarioComunidad usuarioComunidad2 = new UsuarioComunidad.UserComuBuilder(
                COMU_LA_PLAZUELA_5,
                USER_PEPE)
                .portal("portal B")
                .planta("B")
                .roles(ADM.function)
                .build();
        // No portal, no escalera, no puerta.
        UsuarioComunidad usuarioComunidad3 = new UsuarioComunidad.UserComuBuilder(
                COMU_LA_PLAZUELA_5,
                USER_JUAN2)
                .planta("13")
                .roles(INQ.function)
                .build();

        final List<UsuarioComunidad> list = new ArrayList<>(2);
        list.add(usuarioComunidad1);
        list.add(usuarioComunidad2);
        list.add(usuarioComunidad3);
        // Run and check.
        runAndCheckAdapterAndHeader(list, 3);

        runFinalCheckUserComuByComu(
                checkUserComuPortalEscalera(
                        usuarioComunidad1,
                        checkUserComuPlantaPuerta(
                                usuarioComunidad1,
                                checkUserComuByComuRol(usuarioComunidad1)
                        )
                )
        );

        runFinalCheckUserComuByComu(
                checkUserComuPortalNoEscalera(
                        usuarioComunidad2,
                        checkUserComuPlantaNoPuerta(
                                usuarioComunidad2,
                                checkUserComuByComuRol(usuarioComunidad2)
                        )
                )
        );

        runFinalCheckUserComuByComu(
                checkUserComuPlantaNoPuerta(
                        usuarioComunidad3,
                        checkUserComuNoPortalNoEscalera(
                                checkUserComuByComuRol(usuarioComunidad3)
                        )
                )
        );
    }

    @Test
    public void test_OnSuccessComunidadData()
    {
        final String testTxt = "testNombre";
        activity.runOnUiThread(() -> {
            fragment.viewer.onSuccessComunidadData(testTxt);
            assertThat(fragment.viewer.nombreComuView.getText().toString(), is(testTxt));
        });
    }

    // ==================================  HELPERS  =================================

    public void checkAdapterAndHeader(int count)
    {
        waitAtMost(6, SECONDS)
                .until(
                        (Callable<Adapter>) ((AdapterView<? extends Adapter>) fragment.viewer.getViewInViewer())::getAdapter,
                        notNullValue());

        SeeUserComuByComuListAdapter adapter =
                (SeeUserComuByComuListAdapter) HeaderViewListAdapter.class.cast(fragment.viewer.getViewInViewer().getAdapter()).getWrappedAdapter();

        assertThat(adapter.getCount(), is(count));
        waitAtMost(6, SECONDS)
                .until(isViewDisplayedAndPerform(
                        allOf(
                                withId(R.id.see_usercomu_by_comu_list_header),
                                withText(containsString(comu_real.getNombreComunidad())))
                ));
    }

    public void runAndCheckAdapterAndHeader(final List<UsuarioComunidad> list, int rowCount)
    {
        activity.runOnUiThread(() -> fragment.viewer.onSuccessLoadItems(list));
        checkAdapterAndHeader(rowCount);
    }
}