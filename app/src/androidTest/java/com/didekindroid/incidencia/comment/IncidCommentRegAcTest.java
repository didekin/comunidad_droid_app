package com.didekindroid.incidencia.comment;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.app.TaskStackBuilder.create;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidCommentRegAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.testutil.ActivityTestUtil.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 04/02/16
 * Time: 11:29
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class IncidCommentRegAcTest {

    private IncidImportancia incidJuanReal1;
    @Rule
    public IntentsTestRule<IncidCommentRegAc> intentsRule = new IntentsTestRule<IncidCommentRegAc>(IncidCommentRegAc.class) {

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_REAL_JUAN);
                UsuarioComunidad juanReal = userComuDao.seeUserComusByUser().get(0);
                incidJuanReal1 = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(juanReal.getUsuario().getUserName(), "Incidencia Real One", juanReal.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(juanReal)
                        .importancia((short) 3).build();
                incidenciaDao.regIncidImportancia(incidJuanReal1);
                IncidenciaUser incidenciaUser = incidenciaDao.seeIncidsOpenByComu(juanReal.getComunidad().getC_Id()).get(0);
                incidJuanReal1 = incidenciaDao.seeIncidImportancia(incidenciaUser.getIncidencia().getIncidenciaId()).getIncidImportancia();
            } catch (UiException | IOException e) {
                fail();
            }

            if (Build.VERSION.SDK_INT >= LOLLIPOP) {
                Intent intent1 = new Intent(getTargetContext(), IncidSeeByComuAc.class).putExtra(INCID_CLOSED_LIST_FLAG.key, false);
                create(getTargetContext()).addNextIntent(intent1).startActivities();
            }
            return new Intent().putExtra(INCIDENCIA_OBJECT.key, incidJuanReal1.getIncidencia());
        }
    };
    private IncidCommentRegAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = intentsRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            cleanTasks(activity);
        }
        cleanOptions(CLEAN_JUAN);
    }

//    ============================  TESTS  ===================================

    @Test
    public void testOnCreate_1()
    {
        assertThat(activity, notNullValue());
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        assertThat(activity.findViewById(incidCommentRegAcLayout), notNullValue());

        onView(withId(R.id.incid_comment_incidencias_rot)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comment_reg_button)).check(matches(isDisplayed()));
        onView(allOf(
                withId(R.id.incid_comment_ed),
                withHint(R.string.incid_comment_ed_hint)
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.incid_reg_desc_txt),
                withText(incidJuanReal1.getIncidencia().getDescripcion())
        )).check(matches(isDisplayed()));
    }

    @Test
    public void testRegComment_1() throws InterruptedException
    {
        // Descripción de comentario no válido.
        onView(withId(R.id.incid_comment_ed)).perform(typeText("Comment = not valid")).perform(closeSoftKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.incid_comment_reg_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, activity, R.string.incid_comment_label);
    }

    @Test
    public void testRegComment_2() throws InterruptedException
    {
        // Caso OK.
        onView(withId(R.id.incid_comment_ed)).perform(typeText("Comment is now valid")).perform(closeSoftKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.incid_comment_reg_button)).perform(scrollTo(), click());
        // Verificación.
        onView(withId(R.id.incid_comments_see_ac)).check(matches(isDisplayed()));
        intended(hasExtra(INCIDENCIA_OBJECT.key, incidJuanReal1.getIncidencia()));
        // CheckUp.
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }
}