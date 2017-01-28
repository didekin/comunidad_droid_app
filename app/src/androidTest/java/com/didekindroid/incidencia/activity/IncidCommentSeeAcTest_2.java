package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidFragmentTags.incid_comments_see_list_fr_tag;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doComment;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidencia;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.util.UIutils.formatTimeStampToString;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 08/02/16
 * Time: 10:28
 */

/**
 * Tests CON comentarios registrados.
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class IncidCommentSeeAcTest_2 {

    IncidCommentSeeAc mActivity;
    IncidImportancia incidJuanReal1;
    @Rule
    public IntentsTestRule<IncidCommentSeeAc> activityRule = new IntentsTestRule<IncidCommentSeeAc>(IncidCommentSeeAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            // Insertamos comentarios.
            try {
                IncidenciaServ.regIncidComment(doComment("Comment_1_incidjuanReal1", incidJuanReal1.getIncidencia()));
                IncidenciaServ.regIncidComment(doComment("Comment_2_incidjuanReal1", incidJuanReal1.getIncidencia()));
            } catch (UiException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_REAL_JUAN);
                UsuarioComunidad juanReal = userComuDaoRemote.seeUserComusByUser().get(0);
                incidJuanReal1 = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(juanReal.getUsuario().getUserName(), "Incidencia Real One", juanReal.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(juanReal)
                        .importancia((short) 3).build();
                IncidenciaServ.regIncidImportancia(incidJuanReal1);
                IncidenciaUser incidenciaUser = IncidenciaServ.seeIncidsOpenByComu(juanReal.getComunidad().getC_Id()).get(0);
                incidJuanReal1 = IncidenciaServ.seeIncidImportancia(incidenciaUser.getIncidencia().getIncidenciaId()).getIncidImportancia();
            } catch (IOException | UiException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCIDENCIA_OBJECT.key, incidJuanReal1.getIncidencia());
            return intent;
        }
    };
    private IncidCommentSeeAdapter mAdapter;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = activityRule.getActivity();
        Thread.sleep(2000);
        mAdapter = ((IncidCommentSeeListFr) mActivity.getSupportFragmentManager().findFragmentByTag(incid_comments_see_list_fr_tag)).mAdapter;
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN);
    }

    @Test
    public void testOnData_1() throws Exception
    {
        assertThat(mAdapter, notNullValue());
        assertThat(mAdapter.getCount(), is(2));
        IncidComment comment_1 = mAdapter.getItem(0);
        assertThat(comment_1.getCommentId() > 0, is(true));
        assertThat(comment_1.getFechaAlta(), notNullValue());
        assertThat(comment_1.getRedactor(), is(COMU_REAL_JUAN.getUsuario()));
        assertThat(comment_1.getDescripcion(), is("Comment_1_incidjuanReal1"));
        assertThat(mAdapter.getItem(1).getDescripcion(), is("Comment_2_incidjuanReal1"));
    }

    @Test
    public void testOnData_2() throws Exception
    {
        IncidComment comment_1 = mAdapter.getItem(0);

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
}