package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ActivityMock;
import com.didekinlib.model.comunidad.Municipio;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;

import static com.didekindroid.testutil.ActivityTestUtils.checkSpinnerCtrlerLoadItems;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 07/05/17
 * Time: 13:46
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerMunicipioSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);
    CtrlerMunicipioSpinner controller;

    @Before
    public void setUp() throws Exception
    {
        final Activity activity = activityRule.getActivity();
        controller = new CtrlerMunicipioSpinner();
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
    }

    @Test
    public void test_MunicipiosByProvincia() throws Exception
    {
        controller.municipiosByProvincia((short) 11).test().assertOf(new Consumer<TestObserver<List<Municipio>>>() {
            @Override
            public void accept(TestObserver<List<Municipio>> listTestObserver) throws Exception
            {
                assertThat(listTestObserver.values().size(), is(1)); // Single.
                assertThat(listTestObserver.values().get(0).size(), is(44));
                assertThat(listTestObserver.values().get(0).get(0).getNombre(), is("Alcalá de los Gazules"));
            }
        });
    }

    @Test
    public void test_LoadItemsByEntitiyId() throws Exception
    {
        checkSpinnerCtrlerLoadItems(controller, 11L);
    }
}