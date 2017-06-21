package com.didekindroid.comunidad;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
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
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_REAL;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 21/06/17
 * Time: 14:24
 */
@RunWith(AndroidJUnit4.class)
public class ViewerComuSearchResultAcTest {

    AppCompatActivity activity;
    ViewerComuSearchResultAc viewer;

    @Before
    public void setUp()
    {
        Intent intent = new Intent(getTargetContext(), ComuSearchResultsAc.class);
        intent.putExtra(COMUNIDAD_SEARCH.key, COMU_REAL);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        activity = (AppCompatActivity) getInstrumentation().startActivitySync(intent);

        cleanWithTkhandler();
        viewer = newViewerComuSearchResultAc((ComuSearchResultsAc) activity);
    }


    @Test
    public void test_NewViewerComuSearchResultAc() throws Exception
    {
        assertThat(viewer.getController(), notNullValue());
    }

    @Test
    public void test_UpdateActivityMenu() throws Exception
    {
        PopupMenu popupMenu = new PopupMenu(getTargetContext(), null);
        Menu myMenu = popupMenu.getMenu();
        activity.getMenuInflater().inflate(R.menu.menu_mock_one, myMenu);

        MenuItem itemSeeUserComu = myMenu.findItem(R.id.see_usercomu_by_user_ac_mn);

        //Preconditions.
        assertThat(!itemSeeUserComu.isVisible() && !itemSeeUserComu.isEnabled(), is(true));
        assertThat(viewer.getController().isRegisteredUser(), is(false));
        // Exec.
        viewer.updateActivityMenu(myMenu);
        // Check: no change.
        assertThat(!itemSeeUserComu.isVisible() && !itemSeeUserComu.isEnabled(), is(true));

        //Preconditions.
        viewer.getController().updateIsRegistered(true);
        // Exec.
        viewer.updateActivityMenu(myMenu);
        // Check: change both attributes.
        assertThat(itemSeeUserComu.isVisible() && itemSeeUserComu.isEnabled(), is(true));
    }
}