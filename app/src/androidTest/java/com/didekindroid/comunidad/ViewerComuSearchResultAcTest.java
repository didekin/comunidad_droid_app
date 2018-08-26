package com.didekindroid.comunidad;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.comunidad.ViewerComuSearchResultAc.newViewerComuSearchResultAc;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.lib_one.testutil.UiTestUtil.doMockMenu;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


/**
 * User: pedro@didekin
 * Date: 21/06/17
 * Time: 14:24
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class ViewerComuSearchResultAcTest {

    private AppCompatActivity activity;
    private ViewerComuSearchResultAc viewer;

    @Before
    public void setUp()
    {
        cleanWithTkhandler();

        Intent intent = new Intent(getTargetContext(), ComuSearchResultsAc.class)
                .putExtra(COMUNIDAD_SEARCH.key, comu_real)
                .setFlags(FLAG_ACTIVITY_NEW_TASK);
        activity = (AppCompatActivity) getInstrumentation().startActivitySync(intent);
        viewer = newViewerComuSearchResultAc((ComuSearchResultsAc) activity);
    }

    @Test
    public void test_NewViewerComuSearchResultAc()
    {
        assertThat(viewer.getController(), notNullValue());
    }

    @Test
    public void test_UpdateActivityMenu()
    {
        Menu myMenu = doMockMenu(activity, R.menu.menu_mock_one);
        MenuItem itemSeeUserComu = myMenu.findItem(R.id.see_usercomu_by_user_ac_mn);

        //Preconditions.
        assertThat(!itemSeeUserComu.isVisible() && !itemSeeUserComu.isEnabled(), is(true));
        assertThat(viewer.getController().isRegisteredUser(), is(false));
        // Exec.
        viewer.updateActivityMenu(myMenu);
        // Check: no change.
        assertThat(!itemSeeUserComu.isVisible() && !itemSeeUserComu.isEnabled(), is(true));

        //Preconditions.
        viewer.getController().getTkCacher().updateAuthToken("mock_gcmTk");
        // Exec.
        viewer.updateActivityMenu(myMenu);
        // Check: change both attributes.
        assertThat(itemSeeUserComu.isVisible() && itemSeeUserComu.isEnabled(), is(true));
    }
}