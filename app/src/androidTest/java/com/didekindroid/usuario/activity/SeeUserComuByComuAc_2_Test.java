package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

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
import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.external.LongListMatchers.withAdaptedData;
import static com.didekindroid.usuario.activity.utils.RolUi.INQ;
import static com.didekindroid.usuario.activity.utils.RolUi.formatRol;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN2;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_LA_PLAZUELA_5;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN2;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.validaTypedUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
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
    SeeUserComutByComuListAdapter mAdapter;
    long comunidadId;
    Intent intent;

    @Rule
    public ActivityTestRule<SeeUserComuByComuAc> mActivityRule =
            new ActivityTestRule<>(SeeUserComuByComuAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @After
    public void tearDown() throws Exception
    {
    }

//    =================================================================================================================

    @Test
    public void testOneUserComunidadOne() throws Exception
    {
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        doSetUp();
        launch();

        assertThat(mAdapter.getCount(), is(1));
        UsuarioComunidad userComu = mAdapter.getItem(0);
        checkUserComu(userComu, COMU_TRAV_PLAZUELA_PEPE);

        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void testThreeUsersInComunidad() throws IOException, UiException
    {
        // No portal ni escalera.
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        doSetUp();
        // Tiene portal, escalera, planta y puerta.
        ServOne.regUserAndUserComu(new UsuarioComunidad.UserComuBuilder(
                new Comunidad.ComunidadBuilder().c_id(comunidadId).build(),
                COMU_PLAZUELA5_PEPE.getUsuario()).userComuRest(COMU_PLAZUELA5_PEPE)
                .build()).execute();
        // No escalera.
        UsuarioComunidad userComuNew = new UsuarioComunidad.UserComuBuilder(
                new Comunidad.ComunidadBuilder().c_id(comunidadId)
                        .build(),
                USER_JUAN2)
                .portal("portal B")
                .planta("B")
                .puerta("123")
                .roles(INQ.function)
                .build();
        ServOne.regUserAndUserComu(userComuNew).execute();
        launch();

        // Check adapter data.
        assertThat(mAdapter.getCount(), is(3));
        UsuarioComunidad juan1 = mAdapter.getItem(0);
        checkUserComu(juan1, COMU_PLAZUELA5_JUAN);
        UsuarioComunidad juan2 = mAdapter.getItem(1);
        checkUserComu(juan2, userComuNew);
        UsuarioComunidad pepe = mAdapter.getItem(2);
        checkUserComu(pepe, COMU_PLAZUELA5_PEPE);

        // Header.
        onView(withId(R.id.see_usercomu_by_comu_list_header))
                .check(matches(withText(containsString(COMU_PLAZUELA5_JUAN.getComunidad().getNombreComunidad()))));

        // Delete data.
        cleanOptions(CLEAN_JUAN_AND_PEPE);
        cleanOptions(CLEAN_JUAN2);
    }

    @Test
    public void testNoPortalEscalera() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        doSetUp();
        launch();

        // COMU_PLAZUELA5_JUAN: no muestra portal y escalera; muestra aliad, email, planta, puerta y roles.
        checkNotRotulos(R.id.usercomu_item_portal_rot, R.id.usercomu_item_escalera_rot);
        checkRotulos(R.id.usercomu_item_planta_rot, R.id.usercomu_item_puerta_rot);
        checkUser(COMU_PLAZUELA5_JUAN.getUsuario().getAlias(), COMU_PLAZUELA5_JUAN.getUsuario().getUserName());
        checkRole(formatRol(COMU_PLAZUELA5_JUAN.getRoles(), mActivity.getResources()));
        checkPlantaView(COMU_PLAZUELA5_JUAN.getPlanta());
        checkPuertaView(COMU_PLAZUELA5_JUAN.getPuerta());

        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testNoEscalera() throws UiException, IOException
    {
        UsuarioComunidad usuarioComunidad = new UsuarioComunidad.UserComuBuilder(
                COMU_LA_PLAZUELA_5,
                USER_JUAN2)
                .portal("portal B")
                .planta("B")
                .puerta("123")
                .roles(INQ.function)
                .build();
        signUpAndUpdateTk(usuarioComunidad);
        doSetUp();
        launch();

        // Muestra alias, email, portal, planta, puerta y roles. No muestra escalera.
        checkNotRotulos(R.id.usercomu_item_escalera_rot);
        checkRotulos(R.id.usercomu_item_portal_rot, R.id.usercomu_item_planta_rot, R.id.usercomu_item_puerta_rot);
        checkUser(usuarioComunidad.getUsuario().getAlias(), usuarioComunidad.getUsuario().getUserName());
        checkRole(formatRol(usuarioComunidad.getRoles(), mActivity.getResources()));
        checkPortalView(usuarioComunidad.getPortal());
        checkPlantaView(usuarioComunidad.getPlanta());
        checkPuertaView(usuarioComunidad.getPuerta());

        cleanOptions(CLEAN_JUAN2);
    }

    @Test
    public void testTodo() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_PLAZUELA5_PEPE);
        doSetUp();
        launch();

        // No muestra escalera; muestra alias, email, portal, escalera, planta, puerta y roles.

        checkRotulos(R.id.usercomu_item_portal_rot, R.id.usercomu_item_escalera_rot, R.id.usercomu_item_planta_rot, R.id.usercomu_item_puerta_rot);
        checkUser(COMU_PLAZUELA5_PEPE.getUsuario().getAlias(), COMU_PLAZUELA5_PEPE.getUsuario().getUserName());
        checkRole(formatRol(COMU_PLAZUELA5_PEPE.getRoles(), mActivity.getResources()));
        checkPortalView(COMU_PLAZUELA5_PEPE.getPortal());
        checkEscaleraView(COMU_PLAZUELA5_PEPE.getEscalera());
        checkPlantaView(COMU_PLAZUELA5_PEPE.getPlanta());
        checkPuertaView(COMU_PLAZUELA5_PEPE.getPuerta());

        cleanOptions(CLEAN_PEPE);
    }

//    =============================== HELPER METHODS ========================================

    private void doSetUp() throws UiException
    {
        List<UsuarioComunidad> usuariosComu = ServOne.seeUserComusByUser();
        comunidadId = usuariosComu.get(0).getComunidad().getC_Id();
        intent = new Intent();
        intent.putExtra(COMUNIDAD_ID.key, comunidadId);
    }

    private void launch()
    {
        mActivity = mActivityRule.launchActivity(intent);
        mFragment = (SeeUserComuByComuFr) mActivity.getSupportFragmentManager().findFragmentById(R.id.see_usercomu_by_comu_frg);
        mAdapter = mFragment.mAdapter;
    }

    private void checkUserComu(UsuarioComunidad userComuOut, UsuarioComunidad userComuIn)
    {
        validaTypedUsuarioComunidad(userComuOut, userComuIn.getPortal(), userComuIn.getEscalera(), userComuIn.getPlanta(),
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
                withId(R.id.usercomu_item_alias_txt),
                hasSibling(allOf(
                        withId(R.id.usercomu_item_username_txt),
                        withText(userName)
                ))
        )).check(matches(isDisplayed()));
    }

    private void checkRole(String roles)
    {
        onView(allOf(
                withText(roles),
                withId(R.id.usercomu_item_roles_txt)
        )).check(matches(isDisplayed()));
    }

    private void checkPortalView(String portal)
    {
        onView(allOf(
                withText(portal),
                withId(R.id.usercomu_item_portal_txt),
                hasSibling(withId(R.id.usercomu_item_portal_rot))
        )).check(matches(isDisplayed()));
    }

    private void checkEscaleraView(String escalera)
    {
        onView(allOf(
                withText(escalera),
                withId(R.id.usercomu_item_escalera_txt),
                hasSibling(withId(R.id.usercomu_item_escalera_rot))
        )).check(matches(isDisplayed()));
    }

    private void checkPlantaView(String planta)
    {
        onView(allOf(
                withText(planta),
                withId(R.id.usercomu_item_planta_txt),
                hasSibling(withId(R.id.usercomu_item_planta_rot))
        )).check(matches(isDisplayed()));
    }

    private void checkPuertaView(String puerta)
    {
        onView(allOf(
                withText(puerta),
                withId(R.id.usercomu_item_puerta_txt),
                hasSibling(withId(R.id.usercomu_item_puerta_rot))
        )).check(matches(isDisplayed()));
    }
}