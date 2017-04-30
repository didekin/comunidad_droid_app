package com.didekindroid.usuariocomunidad.listbycomu;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComunidadBean;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_LA_PLAZUELA_5;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_REAL;
import static com.didekindroid.testutil.ActivityTestUtils.getAdapter;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN2;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.RolUi.ADM;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRO;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuByComuCommon;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuNoPortalNoEscalera;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuPlantaNoPuerta;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuPlantaPuerta;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuPortalEscalera;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuPortalNoEscalera;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.runFinalCheckUserComuByComu;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 26/03/17
 * Time: 14:54
 */
@RunWith(AndroidJUnit4.class)
public class ViewerSeeUserComuByComuTest {

    final static AtomicReference<String> flagMethod1 = new AtomicReference<>(BEFORE_METHOD_EXEC);
    final static AtomicReference<String> flagMethod2 = new AtomicReference<>(BEFORE_METHOD_EXEC);
    UsuarioComunidad usuarioComunidad;
    @Rule
    public IntentsTestRule<SeeUserComuByComuAc> activityRule = new IntentsTestRule<SeeUserComuByComuAc>(SeeUserComuByComuAc.class, true) {

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_REAL_PEPE);
                usuarioComunidad = userComuDaoRemote.seeUserComusByUser().get(0);
            } catch (IOException | UiException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_ID.key, usuarioComunidad.getComunidad().getC_Id());
            return intent;
        }
    };
    SeeUserComuByComuFr fragment;
    SeeUserComuByComuAc activity;
    ViewerSeeUserComuByComu viewer;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        fragment = (SeeUserComuByComuFr) activity.getSupportFragmentManager().findFragmentById(R.id.see_usercomu_by_comu_frg);

        AtomicReference<ViewerSeeUserComuByComu> viewerAtomic = new AtomicReference<>(null);
        viewerAtomic.compareAndSet(null, fragment.viewer);
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
        viewer = viewerAtomic.get();
        // Esperamos hasta que se ha hecho la 'primera' carga de datos.
        checkAdapterAndHeader(1);
    }

    @After
    public void cleanUp() throws UiException
    {
        cleanOptions(CLEAN_PEPE);
    }

    // ==================================  TESTS  =================================

    @Test
    public void test_NewViewerUserComuByComu() throws Exception
    {
        assertThat(viewer.getViewInViewer(), instanceOf(ListView.class));
        assertThat(viewer.nombreComuView, notNullValue());
        assertThat(viewer.getViewInViewer().getHeaderViewsCount(), is(1));
        assertThat(viewer.getController(), instanceOf(CtrlerUserComuByComuList.class));
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        viewer.setController(new CtrlerUserComuByComuList(viewer) {
            @Override
            public boolean loadItemsByEntitiyId(Long... entityId)
            {
                assertThat(flagMethod1.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return true;
            }

            @Override
            boolean comunidadData(long comunidadId)
            {
                assertThat(flagMethod2.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                return true;
            }
        });
        ComunidadBean comunidadBean = new ComunidadBean();
        comunidadBean.setComunidadId(activity.getIntent().getLongExtra(COMUNIDAD_ID.key, 0L));

        viewer.doViewInViewer(new Bundle(0), comunidadBean);
        assertThat(flagMethod1.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        assertThat(flagMethod2.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
    }

    @Test
    public void test_OnSuccessLoadItems_1() throws Exception
    {
        final List<UsuarioComunidad> list = new ArrayList<>(1);
        list.add(usuarioComunidad);
        // Run and check.
        runAndCheckAdapterAndHeader(list, 1);
    }

    /* Test de presentación de los datos en la pantalla.*/
    @Test
    public void test_OnSuccessLoadItems_2() throws Exception
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
                                checkUserComuByComuCommon(usuarioComunidad1)
                        )
                )
        );

        runFinalCheckUserComuByComu(
                checkUserComuPortalNoEscalera(
                        usuarioComunidad2,
                        checkUserComuPlantaNoPuerta(
                                usuarioComunidad2,
                                checkUserComuByComuCommon(usuarioComunidad2)
                        )
                )
        );

        runFinalCheckUserComuByComu(
                checkUserComuPlantaNoPuerta(
                        usuarioComunidad3,
                        checkUserComuNoPortalNoEscalera(
                                usuarioComunidad3,
                                checkUserComuByComuCommon(usuarioComunidad3)
                        )
                )
        );
    }

    @Test
    public void test_OnSuccessComunidadData() throws Exception
    {
        final String testTxt = "testNombre";
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessComunidadData(testTxt);
                assertThat(viewer.nombreComuView.getText().toString(), is(testTxt));
            }
        });
    }

    // ==================================  HELPERS  =================================

    public void checkAdapterAndHeader(int count)
    {
        waitAtMost(3, SECONDS).until(getAdapter(viewer.getViewInViewer()), notNullValue());
        SeeUserComuByComuListAdapter adapter = (SeeUserComuByComuListAdapter) HeaderViewListAdapter.class.cast(viewer.getViewInViewer().getAdapter()).getWrappedAdapter();
        assertThat(adapter.getCount(), is(count));
        waitAtMost(2, SECONDS).until(isViewDisplayed(allOf(withId(R.id.see_usercomu_by_comu_list_header), withText(containsString(COMU_REAL.getNombreComunidad())))));
    }

    public void runAndCheckAdapterAndHeader(final List<UsuarioComunidad> list, int rowCount)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessLoadItems(list);
            }
        });
        checkAdapterAndHeader(rowCount);
    }
}