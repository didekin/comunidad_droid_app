package com.didekindroid.usuariocomunidad.repository;

import com.didekindroid.DidekinApp;
import com.didekindroid.lib_one.api.CompletableObserverMock;
import com.didekindroid.lib_one.api.MaybeObserverMock;
import com.didekindroid.lib_one.api.SingleObserverMock;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.app.Instrumentation.newApplication;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_EL_ESCORIAL;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.execCheckSchedulersTest;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpMockGcmGetComu;
import static com.didekinlib.model.usuariocomunidad.Rol.PRESIDENTE;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static com.google.firebase.iid.FirebaseInstanceId.getInstance;

/**
 * User: pedro@didekin
 * Date: 25/05/17
 * Time: 09:44
 */
public class CtrlerUsuarioComunidadTest {

    private CtrlerUsuarioComunidad controller;

    @Before
    public void setUp() throws Exception
    {
        getInstrumentation().callApplicationOnCreate(newApplication(DidekinApp.class, getTargetContext()));
        controller = new CtrlerUsuarioComunidad();
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Test
    public void test_DeleteUserComu() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.deleteUserComu(new SingleObserverMock<>(), signUpGetComu(COMU_REAL_PEPE)),
                controller);
    }

    @Test
    public void test_getUserComuByUserAndComu() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.getUserComuByUserAndComu(new MaybeObserverMock<>(), signUpGetComu(COMU_REAL_PEPE)),
                controller);
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_isOldestOrAdmonUserComu() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.isOldestOrAdmonUserComu(new SingleObserverMock<>(), signUpGetComu(COMU_REAL_PEPE)),
                controller);
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_ModifyUserComu() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.modifyUserComu(
                        new SingleObserverMock<>(),
                        new UsuarioComunidad.UserComuBuilder(
                                signUpGetComu(COMU_ESCORIAL_PEPE),
                                null)
                                .userComuRest(COMU_ESCORIAL_PEPE).escalera("new_esc").build()
                ),
                controller
        );
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_regComuAndUserAndUserComu() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.regComuAndUserAndUserComu(new CompletableObserverMock(), COMU_LA_FUENTE_PEPE),
                controller
        );
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_regComuAndUserComu() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.regComuAndUserComu(
                        new CompletableObserverMock(),
                        new UsuarioComunidad.UserComuBuilder(COMU_EL_ESCORIAL, regComuUserUserComuGetUser(COMU_LA_FUENTE_PEPE))
                                .planta("uno")
                                .roles(PROPIETARIO.function)
                                .build()),
                controller
        );
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_regUserAndUserComu() throws Exception
    {
        final Comunidad comunidad = signUpGetComu(COMU_REAL_JUAN);
        cleanWithTkhandler();
        getInstance().deleteInstanceId();
        execCheckSchedulersTest(ctrler -> ctrler.regUserAndUserComu(
                new CompletableObserverMock(),
                new UsuarioComunidad.UserComuBuilder(comunidad, USER_PEPE)
                        .roles(PRESIDENTE.function)
                        .build()
                ),
                controller
        );
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

    @Test
    public void test_regUserComu() throws Exception
    {
        execCheckSchedulersTest(
                ctrler -> ctrler.regUserComu(
                        new CompletableObserverMock(),
                        new UsuarioComunidad.UserComuBuilder(
                                signUpMockGcmGetComu(COMU_REAL_JUAN, "juan_gcm_tk"), regComuUserUserComuGetUser(COMU_LA_FUENTE_PEPE)
                        ).planta("uno").roles(PROPIETARIO.function).build()),
                controller
        );
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }
}