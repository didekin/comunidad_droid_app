package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.v4.app.Fragment;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import java.io.File;
import java.io.IOException;

import static android.database.sqlite.SQLiteDatabase.deleteDatabase;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasKey;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekin.common.oauth2.Rol.PROPIETARIO;
import static com.didekindroid.common.activity.BundleKey.INCID_ACTIVITY_VIEW_ID;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.common.activity.FragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.testutils.ActivityTestUtils.updateSecurityData;
import static com.didekindroid.incidencia.repository.IncidenciaDataDbHelperTest.DB_PATH;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidImportanciaWithId;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.makeUsuarioComunidad;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * User: pedro@didekin
 * Date: 01/04/16
 * Time: 11:32
 */

/**
 * Clase con métodos comunes a los tests de edición de una incidencia sin resolución, con un
 * usuario en sesión sin autoriad 'adm'.
 */
@SuppressWarnings("unchecked")
public abstract class IncidEditAbstractTest {

    UsuarioComunidad pepeUserComu;
    @SuppressWarnings("unused")
    UsuarioComunidad juanUserComu;
    IncidImportancia incidenciaPepe;
    IncidImportancia incidenciaJuan;
    IncidEditAc mActivity;
    Fragment incidEditFr;
    IncidImportancia incidImportanciaIntent;
    boolean flagResolucionIntent;

    @Rule
    public IntentsTestRule<IncidEditAc> intentRule = doIntentRule();

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
        assertThat(mActivity, notNullValue());
        incidEditFr = mActivity.getSupportFragmentManager().findFragmentByTag(incid_edit_ac_frgs_tag);
        assertThat(incidEditFr, notNullValue());
        // Intent extras in activity.
        incidImportanciaIntent = (IncidImportancia) mActivity.getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key);
        flagResolucionIntent = mActivity.getIntent().getBooleanExtra(INCID_RESOLUCION_FLAG.key, false);

        //Premisas.
        if (incidImportanciaIntent.getUserComu().hasAdministradorAuthority() || incidImportanciaIntent.isIniciadorIncidencia()) {
            assertThat(incidEditFr, instanceOf(IncidEditMaxPowerFr.class));
            assertThat(incidEditFr.getArguments(), hasEntry(INCID_RESOLUCION_FLAG.key, is(flagResolucionIntent)));
        } else {
            assertThat(incidEditFr, instanceOf(IncidEditNoPowerFr.class));
            assertThat(incidEditFr.getArguments(), not(hasKey(INCID_RESOLUCION_FLAG.key)));
        }
        assertThat(incidEditFr.getArguments(), allOf(
                hasEntry(INCID_IMPORTANCIA_OBJECT.key, is(incidImportanciaIntent)),
                hasEntry(INCID_ACTIVITY_VIEW_ID.key, is(R.id.incid_edit_fragment_container_ac))
        ));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean());
    }

    @AfterClass
    public static void cleanDbSqlite()
    {
        String dBFileName = DB_PATH.concat(IncidenciaDataDbHelper.DB_NAME);
        deleteDatabase(new File(dBFileName));
    }

    abstract IntentsTestRule<IncidEditAc> doIntentRule();

    abstract CleanUserEnum whatToClean();

    //  ===============================  HELPER METHODS ================================

    @NonNull
    Intent getIntentPepeJuanRealNoPower()
    {
        try {
            signUpAndUpdateTk(COMU_REAL_PEPE);
            pepeUserComu = ServOne.seeUserComusByUser().get(0);
            // Insertamos incidencia.
            IncidenciaUser incidenciaUser_1 = insertGetIncidenciaUser(pepeUserComu, 0);
            incidenciaPepe = IncidenciaServ.seeIncidImportancia(incidenciaUser_1.getIncidencia().getIncidenciaId()).getIncidImportancia();

            // Registro userComu en misma comunidad.
            UsuarioComunidad userComuJuan = makeUsuarioComunidad(pepeUserComu.getComunidad(), USER_JUAN,
                    "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
            ServOne.regUserAndUserComu(userComuJuan).execute();
            updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
            Thread.sleep(1000);
            Incidencia incidencia_2 = insertGetIncidImportanciaWithId(incidenciaPepe.getIncidencia().getIncidenciaId(), userComuJuan, 2).getIncidencia();
            incidenciaJuan = IncidenciaServ.seeIncidImportancia(incidencia_2.getIncidenciaId()).getIncidImportancia();

        } catch (UiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidenciaJuan);
        intent.putExtra(INCID_RESOLUCION_FLAG.key, false);
        return intent;
    }

    void checkScreenEditNoPowerFr()
    {
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        assertThat(mActivity.findViewById(R.id.incid_edit_nopower_fr_layout), notNullValue());
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_nopower_fr_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_rot)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_txt)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_desc_txt)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_ambito_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_importancia_spinner)).check(matches(isDisplayed()));
        onView(allOf(
                withId(R.id.incid_edit_fr_modif_button),
                withText(R.string.incid_importancia_reg_edit_button_rot)
        )).check(matches(isDisplayed()));
    }

    void checkDataEditNoPowerFr()
    {
        onView(allOf(
                withId(R.id.incid_comunidad_txt),
                withText(incidenciaJuan.getIncidencia().getComunidad().getNombreComunidad())
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.incid_reg_desc_txt),
                withText(incidenciaJuan.getIncidencia().getDescripcion())
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.incid_ambito_view),
                withText(new IncidenciaDataDbHelper(mActivity).getAmbitoDescByPk(incidenciaJuan.getIncidencia().getAmbitoIncidencia().getAmbitoId()))
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.incid_reg_importancia_spinner)),
                withText(mActivity.getResources().getStringArray(R.array.IncidImportanciaArray)[incidenciaJuan.getImportancia()])
        )).check(matches(isDisplayed()));
    }

    void checkScreenEditMaxPowerFr()
    {
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        assertThat(mActivity.findViewById(R.id.incid_edit_maxpower_fr_layout), notNullValue());
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_maxpower_fr_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_rot)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_txt)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_desc_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_ambito_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_reg_importancia_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_fr_modif_button)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_txt)).check(matches(isDisplayed()));

        if (incidImportanciaIntent.getUserComu().hasAdministradorAuthority() && !flagResolucionIntent) {
            onView(withId(R.id.incid_edit_fr_borrar_txt)).check(matches(isDisplayed()));
            onView(withId(R.id.incid_edit_fr_borrar_button)).check(matches(isDisplayed()));
        } else {
            onView(withId(R.id.incid_edit_fr_borrar_txt)).check(matches(not(isDisplayed())));
            onView(withId(R.id.incid_edit_fr_borrar_button)).check(matches(not(isDisplayed())));
        }
    }

    void checkDataEditMaxPowerFr(IncidenciaDataDbHelper dBHelper)
    {
        onView(allOf(
                withId(R.id.incid_comunidad_txt),
                withText(incidenciaJuan.getIncidencia().getComunidad().getNombreComunidad())
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.incid_reg_desc_ed),
                withText(incidenciaJuan.getIncidencia().getDescripcion())
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.incid_reg_ambito_spinner)),
                withText(dBHelper.getAmbitoDescByPk(incidenciaJuan.getIncidencia().getAmbitoIncidencia().getAmbitoId()))
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.incid_reg_importancia_spinner)),
                withText(mActivity.getResources().getStringArray(R.array.IncidImportanciaArray)[incidenciaJuan.getImportancia()])
        )).check(matches(isDisplayed()));
    }
}