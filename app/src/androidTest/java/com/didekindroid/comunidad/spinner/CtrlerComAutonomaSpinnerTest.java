package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.comunidad.repository.ComunidadDataDb;
import com.didekinlib.model.comunidad.ComunidadAutonoma;

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
 * Date: 04/05/17
 * Time: 10:09
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerComAutonomaSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerComAutonomaSpinner controller;

    @Before
    public void setUp() throws Exception
    {
        final Activity activity = activityRule.getActivity();
        controller = new CtrlerComAutonomaSpinner();
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
    }

    @Test
    public void test_ComunidadesAutonomasList() throws Exception
    {
        controller.comunidadesAutonomasList().test().assertOf(new Consumer<TestObserver<List<ComunidadAutonoma>>>() {
            @Override
            public void accept(TestObserver<List<ComunidadAutonoma>> listTestObserver) throws Exception
            {
                assertThat(listTestObserver.values().size(), is(1));
                assertThat(listTestObserver.values().get(0).size(), is(ComunidadDataDb.ComunidadAutonoma.NUMBER_RECORDS));
            }
        });
    }

    @Test
    public void test_LoadItemsByEntitiyId() throws Exception
    {
        checkSpinnerCtrlerLoadItems(controller);
    }
}