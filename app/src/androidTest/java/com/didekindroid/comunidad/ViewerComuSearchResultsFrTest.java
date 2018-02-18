package com.didekindroid.comunidad;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.ListMockFr;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Maybe;
import io.reactivex.observers.DisposableMaybeObserver;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_EL_ESCORIAL;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_REAL;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.testutil.ActivityTestUtil.isActivityDying;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpWithTkGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regComu_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regComu_User_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regUserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regUser_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.userComuDataLayout;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static external.LongListMatchers.withAdaptedData;
import static io.reactivex.Maybe.just;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/06/17
 * Time: 11:32
 */
public class ViewerComuSearchResultsFrTest {

    final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public IntentsTestRule<ActivityMock> intentsTestRule = new IntentsTestRule<>(ActivityMock.class, false, true);

    ViewerComuSearchResultsFr viewer;
    ActivityMock activity;
    ListMockFr mockFr;
    boolean isPepeToDelete;

    @Before
    public void setUp()
    {
        activity = intentsTestRule.getActivity();
        mockFr = (ListMockFr) activity.getSupportFragmentManager().findFragmentById(R.id.list_mock_frg);
        viewer = ViewerComuSearchResultsFr.newViewerComuSearchResultsFr(mockFr.getView(), activity);

        isPepeToDelete = false;
    }

    @After
    public void clean() throws UiException
    {
        if (isPepeToDelete) {
            cleanOneUser(USER_PEPE);
        }
        viewer.getController().updateIsRegistered(false);
    }

    @Test
    public void test_NewViewerComuSearchResultsFr() throws Exception
    {
        assertThat(viewer.getController(), isA(CtrlerComunidad.class));
    }

    @Test  // User NOT registered
    public void test_DoViewInViewer_Empty() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            viewer.doViewInViewer(null, COMU_REAL);
        } finally {
            resetAllSchedulers();
        }
        intended(hasExtra(COMUNIDAD_SEARCH.key, COMU_REAL));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(regComu_User_UserComuAcLayout));
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        Comunidad comunidadToSearch = signUpWithTkGetComu(COMU_REAL_PEPE);
        isPepeToDelete = true;

        try {
            trampolineReplaceIoScheduler();
            viewer.doViewInViewer(null, comunidadToSearch);
            assertThat(viewer.getViewInViewer().getOnItemClickListener(), instanceOf(ViewerComuSearchResultsFr.ComuSearchResultListener.class));
        } finally {
            resetAllSchedulers();
        }
        waitAtMost(4, SECONDS).until((Callable<Adapter>) ((AdapterView<? extends Adapter>) viewer.getViewInViewer())::getAdapter, notNullValue());
        assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(1));
        assertThat(viewer.getViewInViewer().getAdapter().getItem(0), is(comunidadToSearch));
    }

    @Test
    public void test_OnSuccessLoadList() throws Exception
    {
        final List<Comunidad> comunidades = asList(COMU_REAL, COMU_EL_ESCORIAL);
        activity.runOnUiThread(() -> viewer.onSuccessLoadList(comunidades));

        waitAtMost(6, SECONDS).until((Callable<Adapter>) ((AdapterView<? extends Adapter>) viewer.getViewInViewer())::getAdapter, notNullValue());
        assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(2));
        onView(withId(android.R.id.list)).check(
                matches(withAdaptedData(is(COMU_REAL))));
        onView(withId(android.R.id.list)).check(
                matches(withAdaptedData(is(COMU_EL_ESCORIAL))));
    }

    @Test   // User IS registered.
    public void test_OnSuccessEmptyList() throws Exception
    {
        viewer.getController().updateIsRegistered(true);
        activity.runOnUiThread(() -> viewer.onSuccessEmptyList(COMU_REAL));

        waitAtMost(4, SECONDS).until(isToastInView(R.string.no_result_search_comunidad, activity));
        intended(hasExtra(COMUNIDAD_SEARCH.key, COMU_REAL));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(regComu_UserComuAcLayout));
        waitAtMost(4, SECONDS).until(isActivityDying(activity));
    }

    @Test
    public void test_ComuSearchResultListener_NOTregistered()
    {

        final ViewerComuSearchResultsFr.ComuSearchResultListener listener = viewer.new ComuSearchResultListener();
        activity.runOnUiThread(() -> {
            viewer.onSuccessLoadList(singletonList(COMU_REAL));
            listener.onItemClick(viewer.getViewInViewer(), new View(activity), 0, 0L);
        });

        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(regUser_UserComuAcLayout));
        intended(hasExtra(COMUNIDAD_LIST_OBJECT.key, COMU_REAL));
    }

    @Test
    public void test_ComuSearchResultListener()
    {
        viewer.setController(new CtrlerComunidad() {
            @Override
            public boolean getUserComu(DisposableMaybeObserver<UsuarioComunidad> observer, Comunidad comunidad)
            {
                assertThat(comunidad, is(COMU_REAL));
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        });
        viewer.getController().updateIsRegistered(true);

        final ViewerComuSearchResultsFr.ComuSearchResultListener listener = viewer.new ComuSearchResultListener();
        activity.runOnUiThread(() -> {
            viewer.onSuccessLoadList(singletonList(COMU_REAL));
            listener.onItemClick(viewer.getViewInViewer(), new View(activity), 0, 0L);
        });

        waitAtMost(4, SECONDS).untilAtomic(flagMethodExec, is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void test_UsuarioComunidadObserverOnSuccess() throws IOException, UiException
    {
        UsuarioComunidad userComu = new UsuarioComunidad.UserComuBuilder(signUpWithTkGetComu(COMU_REAL_PEPE), USER_PEPE).userComuRest(COMU_REAL_PEPE).build();
        isPepeToDelete = true;

        just(userComu).subscribeWith(viewer.new UsuarioComunidadObserver(COMU_REAL));
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(userComuDataLayout));
        intended(hasExtra(USERCOMU_LIST_OBJECT.key, userComu));
    }

    @Test
    public void test_UsuarioComunidadObserverOnComplete() throws IOException, UiException
    {
        Comunidad comunidad = signUpWithTkGetComu(COMU_REAL_PEPE);
        isPepeToDelete = true;
        Maybe.<UsuarioComunidad>empty().subscribeWith(viewer.new UsuarioComunidadObserver(comunidad));
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(regUserComuAcLayout));
        intended(hasExtra(COMUNIDAD_LIST_OBJECT.key, comunidad));
    }
}