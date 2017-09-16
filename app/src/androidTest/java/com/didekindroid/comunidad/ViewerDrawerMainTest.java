package com.didekindroid.comunidad;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekindroid.usuario.testutil.UserItemMenuTestUtils.USER_DATA_AC;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/09/17
 * Time: 13:21
 */
@RunWith(AndroidJUnit4.class)
public class ViewerDrawerMainTest {       // TODO: en algún sitio (DRAWER espresso ? testar que se abre el cajón y enseña el menú.

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, false, true);
    ActivityMock activity;
    ViewerDrawerMain viewerDrawer;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        viewerDrawer = ViewerDrawerMain.newViewerDrawerMain(activity);
    }

    @Test
    public void test_NewViewerDrawerMain() throws Exception
    {
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
    }

    @Test
    public void test_OpenDrawer() throws Exception
    {
    }

    @Test
    public void test_BuildMenu() throws Exception
    {
    }

    //    ============================ MENU ==============================

}