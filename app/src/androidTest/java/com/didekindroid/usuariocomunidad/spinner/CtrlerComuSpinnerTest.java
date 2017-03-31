package com.didekindroid.usuariocomunidad.spinner;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.usuariocomunidad.spinner.CtrlerComuSpinner.newControllerComuSpinner;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 17:53
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerComuSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerComuSpinner controller;

    @Before
    public void setUp() throws IOException, UiException
    {
        final Activity activity = activityRule.getActivity();
        final AtomicReference<CtrlerComuSpinner> atomicController = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                atomicController.compareAndSet(
                        null,
                        (CtrlerComuSpinner) newControllerComuSpinner(
                                new ViewerComuSpinner(new Spinner(activity), activity, null) {
                                    @Override
                                    public long getSelectedItemId()
                                    {
                                        return 123L;
                                    }
                                })
                );
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicController, notNullValue());
        controller = atomicController.get();
    }

    @Test
    public void testOnSuccessLoadDataInSpinner()
    {
        List<Comunidad> comunidades = makeListComu();
        controller.onSuccessLoadDataInSpinner(comunidades);

        assertThat(controller.getSpinnerAdapter().getCount(), is(2));
        assertThat(controller.getSpinnerView().getAdapter(), CoreMatchers.<SpinnerAdapter>is(controller.getSpinnerAdapter()));
        // Assertions based on viewer.getSelectedItemId(): 123L, second in the list.
        assertThat(controller.getSpinnerView().getSelectedItemPosition(), is(1));
    }

    @Test
    public void testLoadDataInSpinner() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_REAL_JUAN);

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.loadDataInSpinner(), is(true));
        } finally {
            reset();
        }

        cleanOneUser(USER_JUAN);
    }

    @Test
    public void getSelectedFromItemId() throws Exception
    {
        List<Comunidad> comunidades = makeListComu();
        controller.getSpinnerAdapter().addAll(comunidades);
        controller.getSpinnerView().setAdapter(controller.getSpinnerAdapter());
        assertThat(controller.getSelectedFromItemId(321L), is(0));
        assertThat(controller.getSelectedFromItemId(123L), is(1));
    }

    @NonNull
    private List<Comunidad> makeListComu()
    {
        List<Comunidad> comunidades = new ArrayList<>(2);
        comunidades.add(new Comunidad.ComunidadBuilder().c_id(321L).nombreVia("AAAAAA")
                .municipio(new Municipio((short) 1, new Provincia((short) 11)))
                .build());
        comunidades.add(new Comunidad.ComunidadBuilder().c_id(123L).nombreVia("ZZZZZ")
                .municipio(new Municipio((short) 2, new Provincia((short) 22)))
                .build());
        return comunidades;
    }
}