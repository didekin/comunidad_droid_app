package com.didekindroid.incidencia.comment;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidCommentRegAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidCommentsSeeFrLayout;
import static com.didekindroid.incidencia.testutils.IncidTestData.doComment;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.util.UiUtil.formatTimeStampToString;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayedAndPerform;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_JUAN;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 08/02/16
 * Time: 10:28
 * <p>
 * Tests CON comentarios registrados.
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class IncidCommentSeeAcTest_2 {

    private IncidCommentSeeAdapter mAdapter;
    private static IncidImportancia incidJuanReal1;

    @Rule
    public IntentsTestRule<IncidCommentSeeAc> activityRule = new IntentsTestRule<IncidCommentSeeAc>(IncidCommentSeeAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            return new Intent().putExtra(INCIDENCIA_OBJECT.key, incidJuanReal1.getIncidencia());
        }
    };

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        incidJuanReal1 = insertGetIncidImportancia(COMU_REAL_JUAN);
        // Insertamos comentarios.
        incidenciaDao.regIncidComment(doComment("Comment_1_incidjuanReal1", incidJuanReal1.getIncidencia()))
                .blockingGet();
        incidenciaDao.regIncidComment(doComment("Comment_2_incidjuanReal1", incidJuanReal1.getIncidencia()))
                .blockingGet();
    }

    @Before
    public void setUp() throws Exception
    {
        mAdapter =
                ((IncidCommentSeeListFr) activityRule.getActivity().getSupportFragmentManager().findFragmentByTag(IncidCommentSeeListFr.class.getName()))
                        .adapter;
        waitAtMost(4, SECONDS).until(() -> mAdapter != null && mAdapter.getCount() == 2);
    }

    @AfterClass
    public static void tearDown()
    {
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testOnData_2()
    {
        IncidComment comment_1 = mAdapter.getItem(0);
        assertThat(comment_1.getCommentId() > 0, is(true));

        onData(is(comment_1)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));
        onView(allOf(
                withText(formatTimeStampToString(comment_1.getFechaAlta())),
                withId(R.id.incid_comment_fecha_view),
                hasSibling(allOf(
                        withId(R.id.incid_comment_redactor_view),
                        withText(COMU_REAL_JUAN.getUsuario().getAlias())
                )),
                hasSibling(allOf(
                        withId(R.id.incid_comment_descripcion_view),
                        withText("Comment_1_incidjuanReal1")
                ))
        )).check(matches(isDisplayed()));

        IncidComment comment_2 = mAdapter.getItem(1);
        onView(allOf(
                withText(formatTimeStampToString(comment_2.getFechaAlta())),
                withId(R.id.incid_comment_fecha_view),
                hasSibling(allOf(
                        withId(R.id.incid_comment_redactor_view),
                        withText(COMU_REAL_JUAN.getUsuario().getAlias())
                )),
                hasSibling(allOf(
                        withId(R.id.incid_comment_descripcion_view),
                        withText("Comment_2_incidjuanReal1")
                ))
        )).check(matches(isDisplayed()));
    }

    @Test
    public void test_newCommentButton()
    {
        waitAtMost(6, SECONDS).until(isViewDisplayedAndPerform(withId(R.id.incid_new_comment_fab), click()));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidCommentRegAcLayout));
        checkUp(incidCommentsSeeFrLayout);
    }
}