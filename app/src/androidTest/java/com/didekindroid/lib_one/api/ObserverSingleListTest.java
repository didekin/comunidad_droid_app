package com.didekindroid.lib_one.api;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_C;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.BAD_REQUEST;
import static io.reactivex.Single.just;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/10/2017
 * Time: 13:21
 */
@RunWith(AndroidJUnit4.class)
public class ObserverSingleListTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    ObserverSingleList<ViewerListTest, String> observer;

    @Before
    public void setUp()
    {
        Intent intent = new Intent(getTargetContext(), ActivityMock.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        AppCompatActivity activity = (AppCompatActivity) getInstrumentation().startActivitySync(intent);
        ViewerListTest viewer = new ViewerListTest(null, activity, null);
        observer = new ObserverSingleList<>(viewer);
    }

    @Test
    public void test_OnSuccess() throws Exception
    {
        just(Arrays.asList("uno", "dos", "tres")).subscribeWith(observer);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_C));
    }

    @Test
    public void test_OnError() throws Exception
    {
        Single.<List<String>>error(new UiException(new ErrorBean(BAD_REQUEST))).subscribeWith(observer);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
    }

    // ==================================  HELPERS  =================================

    static class ViewerListTest extends Viewer<AdapterView, ControllerListIf> implements
            ViewerListIf<AdapterView, ControllerListIf, String> {

        public ViewerListTest(AdapterView view, AppCompatActivity activity, ViewerIf parentViewer)
        {
            super(view, activity, parentViewer);
        }

        @Override
        public void onSuccessLoadItemList(List itemsList)
        {
            assertThat(itemsList.size(), is(3));
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_C), is(BEFORE_METHOD_EXEC));
        }

        @Override
        public void onErrorInObserver(Throwable error)
        {
            assertThat(((UiException) error).getErrorBean().getMessage(), is(BAD_REQUEST.getHttpMessage()));
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
        }
    }
}