package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ActivityMock;
import com.didekinlib.model.comunidad.Provincia;

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
 * Date: 05/05/17
 * Time: 19:13
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerProvinciaSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerProvinciaSpinner controller;

    @Before
    public void setUp() throws Exception
    {
        final Activity activity = activityRule.getActivity();
        controller = new CtrlerProvinciaSpinner();
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
    }

    @Test
    public void test_ProvinciasByComAutonoma() throws Exception
    {
        controller.provinciasByComAutonoma((short) 11).test().assertOf(new Consumer<TestObserver<List<Provincia>>>() {
            @Override
            public void accept(TestObserver<List<Provincia>> listTestObserver) throws Exception
            {
                assertThat(listTestObserver.values().size(), is(1)); // Single.
                assertThat(listTestObserver.values().get(0).size(), is(2));
                assertThat(listTestObserver.values().get(0).get(0).getNombre(), is("Badajoz"));
            }
        });
    }

    @Test
    public void test_LoadItemsByEntitiyId() throws Exception
    {
        checkSpinnerCtrlerLoadItems(controller, 11L);
    }
}