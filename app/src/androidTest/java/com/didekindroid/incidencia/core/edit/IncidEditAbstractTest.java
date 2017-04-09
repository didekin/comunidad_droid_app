package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.v4.app.Fragment;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasKey;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.core.IncidenciaDataDbHelper.DB_NAME;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_ACTIVITY_VIEW_ID;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.makeUsuarioComunidad;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * User: pedro@didekin
 * Date: 01/04/16
 * Time: 11:32
 *
 * Clase con métodos comunes a los tests de edición de una incidencia sin resolución, con un
 * usuario en sesión sin autoriad 'adm'.
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
public abstract class IncidEditAbstractTest {

    @Rule
    public IntentsTestRule<IncidEditAc> intentRule = doIntentRule();

    protected UsuarioComunidad pepeUserComu;
    UsuarioComunidad juanUserComu;
    IncidImportancia incidenciaPepe;
    protected IncidImportancia incidenciaJuan;
    protected IncidEditAc mActivity;
    Fragment incidEditFr;
    IncidImportancia incidImportanciaIntent;
    boolean flagResolucionIntent;
    IncidenciaDataDbHelper dbHelper;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(3000);
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

        dbHelper = new IncidenciaDataDbHelper(mActivity);
    }

    @After
    public void tearDown() throws Exception
    {
        dbHelper.dropAllTables();
        dbHelper.close();
        mActivity.deleteDatabase(DB_NAME);
        cleanOptions(whatToClean());
    }

    protected abstract IntentsTestRule<IncidEditAc> doIntentRule();

    protected abstract UsuarioDataTestUtils.CleanUserEnum whatToClean();

    //  ===============================  HELPER METHODS ================================

    @NonNull
    protected Intent getIntentPepeJuanRealNoPower()
    {
        try {
            signUpAndUpdateTk(COMU_REAL_PEPE);
            pepeUserComu = userComuDaoRemote.seeUserComusByUser().get(0);
            // Insertamos incidencia.
            IncidenciaUser incidenciaUser_1 = insertGetIncidenciaUser(pepeUserComu, 0);
            incidenciaPepe = incidenciaDao.seeIncidImportancia(incidenciaUser_1.getIncidencia().getIncidenciaId()).getIncidImportancia();

            // Registro userComu en misma comunidad.
            UsuarioComunidad userComuJuan = makeUsuarioComunidad(pepeUserComu.getComunidad(), USER_JUAN,
                    "portal", "esc", "plantaX", "door12", PROPIETARIO.function);
            userComuDaoRemote.regUserAndUserComu(userComuJuan).execute();
            updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
            Thread.sleep(1000);
            Incidencia incidencia_2 = insertGetIncidenciaUser(incidenciaPepe.getIncidencia().getIncidenciaId(), userComuJuan, 2).getIncidencia();
            incidenciaJuan = incidenciaDao.seeIncidImportancia(incidencia_2.getIncidenciaId()).getIncidImportancia();

        } catch (InterruptedException | IOException | UiException e) {
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
//        assertThat(activity.findViewById(R.id.incid_edit_nopower_fr_layout), notNullValue());
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

    protected void checkDataEditNoPowerFr()
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
                withText(dbHelper.getAmbitoDescByPk(incidenciaJuan.getIncidencia().getAmbitoIncidencia().getAmbitoId()))
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

    void checkDataEditMaxPowerFr()
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
                withText(dbHelper.getAmbitoDescByPk(incidenciaJuan.getIncidencia().getAmbitoIncidencia().getAmbitoId()))
        )).check(matches(isDisplayed()));

        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.incid_reg_importancia_spinner)),
                withText(mActivity.getResources().getStringArray(R.array.IncidImportanciaArray)[incidenciaJuan.getImportancia()])
        )).check(matches(isDisplayed()));
    }
}
