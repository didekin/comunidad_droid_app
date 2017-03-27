package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.comunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.*;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regTwoUserComuSameUser;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 19:13
 */
@RunWith(AndroidJUnit4.class)
public class ViewerComuSpinnerTest {

    public static final long comuId_intent = 321L;

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = new Intent();
            intent.putExtra(COMUNIDAD_ID.key, comuId_intent);
            return intent;
        }

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regTwoUserComuSameUser(UserComuDataTestUtil.makeListTwoUserComu());
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    ViewerComuSpinner viewer;
    Activity activity;
    Spinner spinner;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        spinner = (Spinner) activity.findViewById(R.id.comunidad_spinner);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer = newViewerComuSpinner(spinner, activity, null);
            }
        });
    }

    @Test
    public void testNewViewerComuSpinner() throws Exception
    {
        ViewerComuSpinner viewer = newViewerComuSpinner(spinner, activity, null);
        assertThat(viewer, notNullValue());
        assertThat(viewer.getController(), notNullValue());
    }

    @Test
    public void testInitSelectedItem_intent() throws Exception
    {
        viewer.initSelectedItem(null);
        // Value in mock manager.
        assertThat(viewer.getSelectedItem(), is(comuId_intent));
    }

    @Test
    public void testInitSelectedItem_savedState() throws Exception
    {
        Bundle savedState = new Bundle();
        savedState.putLong(COMUNIDAD_ID.key, 8L);
        viewer.initSelectedItem(savedState);
        assertThat(viewer.getSelectedItem(), is(8L));
    }

    @Test
    public void testSavedState() throws Exception
    {
        viewer.itemSelectedId = 111L;
        Bundle newBundle = new Bundle();
        viewer.saveState(newBundle);
        assertThat(newBundle.getLong(COMUNIDAD_ID.key), is(111L));
    }

    @Test
    public void testGetSelectedItem() throws Exception
    {
        viewer.itemSelectedId = 111L;
        assertThat(viewer.getSelectedItem(), is(111L));
    }

    @Test
    public void testGetItemIdInIntent() throws Exception
    {
       assertThat(viewer.getItemIdInIntent(), is(comuId_intent));
    }

    @Test
    public void doViewInViewer() throws Exception
    {
        // TODO. terminar.
        /*view.setOnItemSelectedListener(new ComuSelectedListener());
        initSelectedItem(savedState);
        controller.loadDataInSpinner();*/

        // Tested in the fragments.
        cleanOneUser(USER_JUAN);
    }
}