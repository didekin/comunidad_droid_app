package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.comunidad.Comunidad;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekindroid.comunidad.ComuBundleKey;
import com.didekinaar.usuario.testutil.UsuarioTestUtils;
import com.didekindroid.comunidad.testutil.ComuTestUtil;
import com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil;
import com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_JUAN2;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekinaar.usuariocomunidad.RolUi.INQ;
import static com.didekinaar.usuariocomunidad.RolUi.formatRolToString;
import static com.external.LongListMatchers.withAdaptedData;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/08/15
 * Time: 11:38
 */
@RunWith(AndroidJUnit4.class)
public class SeeUserComuByComuAc_2_Test {

    SeeUserComuByComuAc mActivity;
    SeeUserComuByComuFr mFragment;
    SeeUserComuByComuListAdapter mAdapter;
    long comunidadId;
    Intent intent;

    @Rule
    public ActivityTestRule<SeeUserComuByComuAc> mActivityRule =
            new ActivityTestRule<>(SeeUserComuByComuAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @After
    public void tearDown() throws Exception
    {
    }

//    =================================================================================================================

    @Test
    public void testOneUserComunidadOne() throws Exception
    {
        UserComuTestUtil.signUpAndUpdateTk(UserComuTestUtil.COMU_TRAV_PLAZUELA_PEPE);
        doSetUp();
        launch();

        assertThat(mAdapter.getCount(), is(1));
        UsuarioComunidad userComu = mAdapter.getItem(0);
        checkUserComu(userComu, UserComuTestUtil.COMU_TRAV_PLAZUELA_PEPE);

        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void testThreeUsersInComunidad() throws IOException, UiException, InterruptedException
    {
        // No portal ni escalera.
        UserComuTestUtil.signUpAndUpdateTk(UserComuTestUtil.COMU_PLAZUELA5_JUAN);
        doSetUp();
        // Tiene portal, escalera, planta y puerta.
        AarUserComuServ.regUserAndUserComu(new UsuarioComunidad.UserComuBuilder(
                new Comunidad.ComunidadBuilder().c_id(comunidadId).build(),
                UserComuTestUtil.COMU_PLAZUELA5_PEPE.getUsuario()).userComuRest(UserComuTestUtil.COMU_PLAZUELA5_PEPE)
                .build()).execute();
        // No escalera.
        UsuarioComunidad userComuNew = new UsuarioComunidad.UserComuBuilder(
                new Comunidad.ComunidadBuilder().c_id(comunidadId)
                        .build(),
                UsuarioTestUtils.USER_JUAN2)
                .portal("portal B")
                .planta("B")
                .puerta("123")
                .roles(INQ.function)
                .build();
        AarUserComuServ.regUserAndUserComu(userComuNew).execute();
        launch();

        // Check adapter data.
        assertThat(mAdapter.getCount(), is(3));
        UsuarioComunidad juan1 = mAdapter.getItem(0);
        checkUserComu(juan1, UserComuTestUtil.COMU_PLAZUELA5_JUAN);
        UsuarioComunidad juan2 = mAdapter.getItem(1);
        checkUserComu(juan2, userComuNew);
        UsuarioComunidad pepe = mAdapter.getItem(2);
        checkUserComu(pepe, UserComuTestUtil.COMU_PLAZUELA5_PEPE);

        // Header.
        onView(ViewMatchers.withId(R.id.see_usercomu_by_comu_list_header))
                .check(matches(withText(containsString(UserComuTestUtil.COMU_PLAZUELA5_JUAN.getComunidad().getNombreComunidad()))));

        // Delete data.
        cleanOptions(CLEAN_JUAN_AND_PEPE);
        cleanOptions(CLEAN_JUAN2);
    }

    @Test
    public void testNoPortalEscalera() throws IOException, UiException, InterruptedException
    {
        UserComuTestUtil.signUpAndUpdateTk(UserComuTestUtil.COMU_PLAZUELA5_JUAN);
        doSetUp();
        launch();

        // COMU_PLAZUELA5_JUAN: no muestra portal y escalera; muestra aliad, email, planta, puerta y roles.
        checkNotRotulos(R.id.usercomu_item_portal_rot, R.id.usercomu_item_escalera_rot);
        checkRotulos(R.id.usercomu_item_planta_rot, R.id.usercomu_item_puerta_rot);
        checkUser(UserComuTestUtil.COMU_PLAZUELA5_JUAN.getUsuario().getAlias(), UserComuTestUtil.COMU_PLAZUELA5_JUAN.getUsuario().getUserName());
        checkRole(formatRolToString(UserComuTestUtil.COMU_PLAZUELA5_JUAN.getRoles(), mActivity.getResources()));
        checkPlantaView(UserComuTestUtil.COMU_PLAZUELA5_JUAN.getPlanta());
        checkPuertaView(UserComuTestUtil.COMU_PLAZUELA5_JUAN.getPuerta());

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testNoEscalera() throws UiException, IOException, InterruptedException
    {
        UsuarioComunidad usuarioComunidad = new UsuarioComunidad.UserComuBuilder(
                ComuTestUtil.COMU_LA_PLAZUELA_5,
                UsuarioTestUtils.USER_JUAN2)
                .portal("portal B")
                .planta("B")
                .puerta("123")
                .roles(INQ.function)
                .build();
        UserComuTestUtil.signUpAndUpdateTk(usuarioComunidad);
        doSetUp();
        launch();

        // Muestra alias, email, portal, planta, puerta y roles. No muestra escalera.
        checkNotRotulos(R.id.usercomu_item_escalera_rot);
        checkRotulos(R.id.usercomu_item_portal_rot, R.id.usercomu_item_planta_rot, R.id.usercomu_item_puerta_rot);
        checkUser(usuarioComunidad.getUsuario().getAlias(), usuarioComunidad.getUsuario().getUserName());
        checkRole(formatRolToString(usuarioComunidad.getRoles(), mActivity.getResources()));
        checkPortalView(usuarioComunidad.getPortal());
        checkPlantaView(usuarioComunidad.getPlanta());
        checkPuertaView(usuarioComunidad.getPuerta());

        cleanOptions(CLEAN_JUAN2);
    }

    @Test
    public void testTodo() throws IOException, UiException, InterruptedException
    {
        UserComuTestUtil.signUpAndUpdateTk(UserComuTestUtil.COMU_PLAZUELA5_PEPE);
        doSetUp();
        launch();

        /* No muestra escalera; muestra alias, email, portal, escalera, planta, puerta y roles.*/
        checkRotulos(R.id.usercomu_item_portal_rot, R.id.usercomu_item_escalera_rot, R.id.usercomu_item_planta_rot, R.id.usercomu_item_puerta_rot);
        checkUser(UserComuTestUtil.COMU_PLAZUELA5_PEPE.getUsuario().getAlias(), UserComuTestUtil.COMU_PLAZUELA5_PEPE.getUsuario().getUserName());
        checkRole(formatRolToString(UserComuTestUtil.COMU_PLAZUELA5_PEPE.getRoles(), mActivity.getResources()));
        checkPortalView(UserComuTestUtil.COMU_PLAZUELA5_PEPE.getPortal());
        checkEscaleraView(UserComuTestUtil.COMU_PLAZUELA5_PEPE.getEscalera());
        checkPlantaView(UserComuTestUtil.COMU_PLAZUELA5_PEPE.getPlanta());
        checkPuertaView(UserComuTestUtil.COMU_PLAZUELA5_PEPE.getPuerta());

        cleanOptions(CLEAN_PEPE);
    }

//    =============================== HELPER METHODS ========================================

    private void doSetUp() throws UiException
    {
        List<UsuarioComunidad> usuariosComu = AarUserComuServ.seeUserComusByUser();
        comunidadId = usuariosComu.get(0).getComunidad().getC_Id();
        intent = new Intent();
        intent.putExtra(ComuBundleKey.COMUNIDAD_ID.key, comunidadId);
    }

    private void launch() throws InterruptedException
    {
        mActivity = mActivityRule.launchActivity(intent);
        mFragment = (SeeUserComuByComuFr) mActivity.getSupportFragmentManager().findFragmentById(R.id.see_usercomu_by_comu_frg);
        Thread.sleep(3000);
        mAdapter = mFragment.mAdapter;
    }

    private void checkUserComu(UsuarioComunidad userComuOut, UsuarioComunidad userComuIn)
    {
        UserComuEspressoTestUtil.validaTypedUsuarioComunidad(userComuOut, userComuIn.getPortal(), userComuIn.getEscalera(), userComuIn.getPlanta(),
                userComuIn.getPuerta(), userComuIn.getRoles());
        assertThat(userComuOut.getComunidad(), is(new Comunidad.ComunidadBuilder().c_id(comunidadId).build()));

        onView(withAdaptedData(Matchers.<Object>is(userComuOut))).check(matches(isDisplayed()));
    }

    private void checkRotulos(Integer... resourcesId)
    {
        for (Integer resource : resourcesId) {
            onView(withId(resource)).check(matches(isDisplayed()));
        }
    }

    private void checkNotRotulos(Integer... resourcesId)
    {
        for (Integer resource : resourcesId) {
            onView(withId(resource)).check(matches(not(isDisplayed())));
        }
    }

    private void checkUser(String alias, String userName)
    {
        onView(allOf(
                withText(alias),
                ViewMatchers.withId(R.id.usercomu_item_alias_txt),
                hasSibling(allOf(
                        ViewMatchers.withId(R.id.usercomu_item_username_txt),
                        withText(userName)
                ))
        )).check(matches(isDisplayed()));
    }

    private void checkRole(String roles)
    {
        onView(allOf(
                withText(roles),
                ViewMatchers.withId(R.id.usercomu_item_roles_txt)
        )).check(matches(isDisplayed()));
    }

    private void checkPortalView(String portal)
    {
        onView(allOf(
                withText(portal),
                ViewMatchers.withId(R.id.usercomu_item_portal_txt),
                hasSibling(ViewMatchers.withId(R.id.usercomu_item_portal_rot))
        )).check(matches(isDisplayed()));
    }

    private void checkEscaleraView(String escalera)
    {
        onView(allOf(
                withText(escalera),
                ViewMatchers.withId(R.id.usercomu_item_escalera_txt),
                hasSibling(ViewMatchers.withId(R.id.usercomu_item_escalera_rot))
        )).check(matches(isDisplayed()));
    }

    private void checkPlantaView(String planta)
    {
        onView(allOf(
                withText(planta),
                ViewMatchers.withId(R.id.usercomu_item_planta_txt),
                hasSibling(ViewMatchers.withId(R.id.usercomu_item_planta_rot))
        )).check(matches(isDisplayed()));
    }

    private void checkPuertaView(String puerta)
    {
        onView(allOf(
                withText(puerta),
                ViewMatchers.withId(R.id.usercomu_item_puerta_txt),
                hasSibling(ViewMatchers.withId(R.id.usercomu_item_puerta_rot))
        )).check(matches(isDisplayed()));
    }
}