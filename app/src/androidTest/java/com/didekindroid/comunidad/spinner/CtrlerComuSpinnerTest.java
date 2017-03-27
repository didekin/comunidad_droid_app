package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.didekindroid.api.ActivityMock;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static org.hamcrest.CoreMatchers.is;
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
    public void setUp(){
        final Activity activity = activityRule.getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                controller = (CtrlerComuSpinner) CtrlerComuSpinner.newControllerComuSpinner(new ViewerComuSpinner(new Spinner(activity), activity, null){
                    @Override
                    public long getSelectedItem()
                    {
                        return 123L;
                    }
                });
            }
        });
    }

    @Test
    public void testProcessBackLoadDataInSpinner()
    {
        List<Comunidad> comunidades = makeListComu();
        controller.onSuccessLoadDataInSpinner(comunidades);

        assertThat(controller.spinnerAdapter.getCount(), is(2));
        assertThat(controller.spinnerView.getAdapter(), CoreMatchers.<SpinnerAdapter>is(controller.spinnerAdapter));
        // Assertions based on viewer.getSelectedItem(): 123L, second in the list.
        assertThat(controller.spinnerView.getSelectedItemPosition(), is(1));
    }

    @Test
    public void loadDataInSpinner()
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.loadDataInSpinner(), is(true));
        } finally {
            reset();
        }
    }

    @Test
    public void getSelectedFromItemId() throws Exception
    {
        List<Comunidad> comunidades = makeListComu();
        controller.spinnerAdapter.addAll(comunidades);
        controller.spinnerView.setAdapter(controller.spinnerAdapter);
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