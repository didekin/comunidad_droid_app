package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ActivityNextMock;
import com.didekindroid.api.ListMockFr;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Maybe;
import io.reactivex.observers.DisposableMaybeObserver;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_EL_ESCORIAL;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_REAL;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.testutil.ActivityTestUtils.getAdapter;
import static com.didekindroid.testutil.ActivityTestUtils.isActivityDying;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regComu_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regComu_User_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regUserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.regUser_UserComuAcLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.userComuDataLayout;
import static com.didekindroid.usuariocomunidad.util.UserComuBundleKey.USERCOMU_LIST_OBJECT;
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
        mockFr = new ListMockFr();
        final AtomicBoolean isRun = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.mock_ac_layout, mockFr, "mockFr")
                        .commitNow();
                mockFr = (ListMockFr) activity.getSupportFragmentManager().findFragmentByTag("mockFr");
                isRun.compareAndSet(false, true);
            }
        });
        waitAtMost(4, SECONDS).untilTrue(isRun);
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
        waitAtMost(4, SECONDS).until(getAdapter(viewer.getViewInViewer()), notNullValue());
        assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(1));
        assertThat((Comunidad) viewer.getViewInViewer().getAdapter().getItem(0), is(comunidadToSearch));
    }

    @Test
    public void test_ReplaceComponent() throws Exception
    {
        viewer.replaceComponent(new Bundle(0), ActivityNextMock.class);
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(R.id.next_mock_ac_layout));
    }

    @Test
    public void test_OnSuccessLoadList() throws Exception
    {
        final List<Comunidad> comunidades = asList(COMU_REAL, COMU_EL_ESCORIAL);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessLoadList(comunidades);
            }
        });

        waitAtMost(4, SECONDS).until(getAdapter(viewer.getViewInViewer()), notNullValue());
        assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(2));
        onView(withId(android.R.id.list)).check(
                matches(withAdaptedData(Matchers.<Object>is(COMU_REAL))));
        onView(withId(android.R.id.list)).check(
                matches(withAdaptedData(Matchers.<Object>is(COMU_EL_ESCORIAL))));
    }

    @Test   // User IS registered.
    public void test_OnSuccessEmptyList() throws Exception
    {
        viewer.getController().updateIsRegistered(true);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessEmptyList(COMU_REAL);
            }
        });

        waitAtMost(4, SECONDS).until(isToastInView(R.string.no_result_search_comunidad, activity));
        intended(hasExtra(COMUNIDAD_SEARCH.key, COMU_REAL));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(regComu_UserComuAcLayout));
        waitAtMost(4, SECONDS).until(isActivityDying(activity));
    }

    @Test
    public void test_ComuSearchResultListener_NOTregistered()
    {

        final ViewerComuSearchResultsFr.ComuSearchResultListener listener = viewer.new ComuSearchResultListener();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessLoadList(singletonList(COMU_REAL));
                listener.onItemClick(viewer.getViewInViewer(), new View(activity), 0, 0L);
            }
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
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessLoadList(singletonList(COMU_REAL));
                listener.onItemClick(viewer.getViewInViewer(), new View(activity), 0, 0L);
            }
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