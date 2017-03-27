package com.didekindroid.usuario.userdata;

import android.support.test.rule.ActivityTestRule;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;

import static com.didekindroid.security.OauthTokenReactor.tokenReactor;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuario.userdata.CtrlerUserData.userDataLoaded;
import static com.didekindroid.usuario.userdata.CtrlerUserData.userDataModified;
import static com.didekindroid.usuario.userdata.CtrlerUserData.userModifiedCacheUpdated;
import static com.didekindroid.usuario.userdata.CtrlerUserData.userModifiedTokenUpdated;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 14:29
 */
public class CtrlerUserDataTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerUserData controller;

    @Before
    public void setUp() throws Exception
    {
        controller = new CtrlerUserData(new ViewerUserData(null, activityRule.getActivity()));
    }

    @Test
    public void testLoadUserData() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.loadUserData(), is(true));
        } finally {
            reset();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void testModifyUser() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.modifyUser(
                    USER_DROID,
                    new Usuario.UsuarioBuilder().copyUsuario(USER_DROID)
                            .userName("new_user_droid")
                            .build()
                    ),
                    is(true));
        } finally {
            reset();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void testProcessBackUserDataLoaded() throws Exception
    {
        controller.onSuccessUserDataLoaded(USER_DROID);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testProcessBackUserModified() throws Exception
    {
        controller.onCompleteUserModified();
        // TODO.....
    }

    // ..................................... OBSERVABLES ..........................................

    @Test
    public void testUserDataLoaded() throws IOException, UiException
    {

        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        // Caso OK.
        userDataLoaded().test().assertResult(USER_PEPE);
        cleanOneUser(USER_PEPE);
    }

    @Test
    public void testUserDataModified() throws IOException, UiException
    {
        // Caso OK.
        Usuario usuario = new Usuario.UsuarioBuilder().userName("new_pepe_name")
                .uId(signUpAndUpdateTk(COMU_ESCORIAL_PEPE).getuId())
                .build();
        userDataModified(TKhandler.getTokenCache().get(), usuario).test().assertResult(1);

        try { // Para poder borrar al usuario.
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            tokenReactor.updateTkAndCacheFromUser(new Usuario.UsuarioBuilder().userName("new_pepe_name").password(USER_PEPE.getPassword()).build());
        } finally {
            reset();
        }
        usuarioDao.deleteUser();
        cleanWithTkhandler();
    }

    @Test
    public void testUserModifiedTokenUpdated() throws IOException, UiException
    {
        /* Caso OK.*/
        final AtomicReference<SpringOauthToken> atomicToken = new AtomicReference<>();

        Usuario newUser = new Usuario.UsuarioBuilder().alias("new_pepe_alias")
                .userName(USER_PEPE.getUserName())
                .uId(signUpAndUpdateTk(COMU_ESCORIAL_PEPE).getuId())
                .password(USER_PEPE.getPassword())
                .build();

        final SpringOauthToken oauthToken1 = TKhandler.getTokenCache().get();
        userModifiedTokenUpdated(oauthToken1, newUser).test().assertValueCount(1).assertOf(new Consumer<TestObserver<SpringOauthToken>>() {
            @Override
            public void accept(TestObserver<SpringOauthToken> testObserver) throws Exception
            {
                final SpringOauthToken oauthToken2 = testObserver.values().get(0);
                atomicToken.compareAndSet(null, oauthToken2);

                assertThat(oauthToken2, notNullValue());
                assertThat(oauthToken2.getValue(), allOf(
                        notNullValue(),
                        not(is(oauthToken1.getValue()))
                ));
                assertThat(oauthToken2.getRefreshToken().getValue(), allOf(
                        notNullValue(),
                        not(is(oauthToken1.getRefreshToken().getValue()))
                ));
            }
        });

        // Para poder borrar el usuario, actualizamos cache.
        TKhandler.initIdentityCache(atomicToken.getAndSet(null));
        usuarioDao.deleteUser();
        cleanWithTkhandler();
    }

    @Test
    public void testUserModifiedCacheUpdated() throws IOException, UiException
    {
        /* Caso OK.*/
        userModifiedCacheUpdated(
                USER_PEPE,
                new Usuario.UsuarioBuilder()
                        .userName("new_pepe_name")
                        .uId(signUpAndUpdateTk(COMU_ESCORIAL_PEPE).getuId())
                        .password(USER_PEPE.getPassword())
                        .build()
        ).test().awaitDone(4, SECONDS).assertComplete();
        assertThat(TKhandler.getTokenCache().get().getValue(), notNullValue());

        // El hecho de poder borrar implica que la cache se ha actualizado correctamente.
        usuarioDao.deleteUser();
        cleanWithTkhandler();
    }
}