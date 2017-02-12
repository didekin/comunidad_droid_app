package com.didekindroid.usuario.userdata;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekindroid.security.Oauth2DaoRemote;
import com.didekindroid.security.OauthTokenReactorIf;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenReactor.tokenReactor;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuario.userdata.UserDataReactor.tokenCacher;
import static com.didekindroid.usuario.userdata.UserDataReactor.userDataLoaded;
import static com.didekindroid.usuario.userdata.UserDataReactor.userDataModified;
import static com.didekindroid.usuario.userdata.UserDataReactor.userDataReactor;
import static com.didekindroid.usuario.userdata.UserDataReactor.userModifiedCacheUpdated;
import static com.didekindroid.usuario.userdata.UserDataReactor.userModifiedUpdatedToken;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 07/02/17
 * Time: 14:13
 */
@RunWith(AndroidJUnit4.class)
public class UserDataReactorTest {

    Usuario usuarioOrig;
    OauthTokenReactorIf oauthTokenReactor = tokenReactor;
    SpringOauthToken oauthToken1;

    @Before
    public void doFixture() throws IOException, UiException
    {
        usuarioOrig = signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        oauthToken1 = tokenCacher.getTokenCache().get();
    }

    @After
    public void unDoFixture() throws UiException
    {
        usuarioDao.deleteUser();
        cleanWithTkhandler();
    }

    @AfterClass
    public static void resetScheduler()
    {
        reset();
    }

    //  =======================================================================================
    // ............................ OBSERVABLES ..................................
    //  =======================================================================================

    @Test
    public void testUserDataLoaded() throws UiException
    {   // Caso OK.
        userDataLoaded().test().assertResult(USER_PEPE);
    }

    @Test
    public void testUserDataModified_1() throws Exception
    {   // Caso OK.
        Usuario usuario = new Usuario.UsuarioBuilder().userName("new_pepe_name").uId(usuarioOrig.getuId()).build();
        userDataModified(oauthToken1, usuario).test().assertResult(1);

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            oauthTokenReactor.updateTkAndCacheFromUser(new Usuario.UsuarioBuilder().userName("new_pepe_name").password(USER_PEPE.getPassword()).build());
        } finally {
            reset();
        }
    }

    /* Caso: no existe uId en database. */
    @Test
    public void testUserDataModified_2() throws Exception
    {
        Usuario usuario = new Usuario.UsuarioBuilder().userName("new_pepe_name").uId(999L).build();
        userDataModified(oauthToken1, usuario).test().assertFailure(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception
            {
                UiException ui = (UiException) throwable;
                return ui.getErrorBean().getMessage().equals(USER_NAME_NOT_FOUND.getHttpMessage());
            }
        });
    }

    @Test
    public void testUserModifiedUpdatedToken_1()
    {   // Caso OK.

        final AtomicReference<SpringOauthToken> atomicToken = new AtomicReference<>();

        Usuario newUser = new Usuario.UsuarioBuilder().alias("new_pepe_alias")
                .userName(USER_PEPE.getUserName())
                .uId(usuarioOrig.getuId())
                .password(USER_PEPE.getPassword())
                .build();

        userModifiedUpdatedToken(oauthToken1, newUser).test().assertValueCount(1).assertOf(new Consumer<TestObserver<SpringOauthToken>>() {
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
        tokenCacher.initIdentityCache(atomicToken.getAndSet(null));
    }

    /* Caso: wrong password in newUser. */
    @Test
    public void testUserModifiedUpdatedToken_2() throws UiException
    {
        Usuario newUser = new Usuario.UsuarioBuilder()
                .userName("new_pepe_name")
                .uId(usuarioOrig.getuId())
                .password("wrong_password")
                .build();

        userModifiedUpdatedToken(oauthToken1, newUser).test().assertFailure(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception
            {
                UiException ui = (UiException) throwable;
                return ui.getErrorBean().getMessage().equals(BAD_REQUEST.getHttpMessage());
            }
        });

        // Para poder borrar el usuario, actualizamos cache: la modificación de userName se hace con éxito y borra accessToken en DB.
        tokenCacher.initIdentityCache(Oauth2DaoRemote.Oauth2.getPasswordUserToken("new_pepe_name", USER_PEPE.getPassword()));
    }

    @Test
    public void testUserModifiedCacheUpdated_1() throws Exception
    {   /* Caso OK.*/

        userModifiedCacheUpdated(
                USER_PEPE,
                new Usuario.UsuarioBuilder()
                        .userName("new_pepe_name")
                        .uId(usuarioOrig.getuId())
                        .password(USER_PEPE.getPassword())
                        .build()
        ).test().awaitDone(4, SECONDS).assertComplete();

        assertThat(tokenCacher.getTokenCache().get().getValue(), notNullValue());
    }

    /* Caso: wrong password in oldUser (and necessarily in newUser too). */
    @Test
    public void testUserModifiedCacheUpdated_2() throws Exception
    {
        Usuario oldUser = new Usuario.UsuarioBuilder().copyUsuario(USER_PEPE).password("wrong_password").build();

        Usuario newUser = new Usuario.UsuarioBuilder()
                .userName("new_pepe_name")
                .uId(usuarioOrig.getuId())
                .password("wrong_password")
                .build();

        userModifiedCacheUpdated(oldUser, newUser)
                .test()
                .assertFailure(
                        new Predicate<Throwable>() {
                            @Override
                            public boolean test(Throwable throwable) throws Exception
                            {
                                UiException ui = (UiException) throwable;
                                return ui.getErrorBean().getMessage().equals(BAD_REQUEST.getHttpMessage());
                            }
                        }
                );

        // Como falla la 1ª actualización del oauthToken, no modifica el accessToken en remoto, ni en local, ni modifica el usuario.
        // Se puede borrar con los datos de la cache inicial.
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Test
    public void testLoadUserData_1() throws UiException
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(userDataReactor.loadUserData(doUserDataController()), is(true));
        } finally {
            reset();
        }
    }

    @Test
    public void testModifyUser_1() throws UiException
    {
        Usuario newUser = new Usuario.UsuarioBuilder()
                .userName("new_pepe_name")
                .uId(usuarioOrig.getuId())
                .password(USER_PEPE.getPassword())
                .build();
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(userDataReactor.modifyUser(doUserDataController(), USER_PEPE, newUser), is(true));
        } finally {
            reset();
        }
    }

    /* Caso NOT OK: wrong password. */
    @Test
    public void testModifyUser_2() throws UiException
    {
        Usuario oldUser = new Usuario.UsuarioBuilder().copyUsuario(USER_PEPE).password("wrong_password").build();

        Usuario newUser = new Usuario.UsuarioBuilder()
                .userName("new_pepe_name")
                .uId(usuarioOrig.getuId())
                .password("wrong_password")
                .build();
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(userDataReactor.modifyUser(doUserDataController(), oldUser, newUser), is(true));
        } finally {
            reset();
        }
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    UserDataControllerIf doUserDataController()
    {

        return new UserDataControllerIf() {
            @Override
            public boolean checkUserData()
            {
                return false;
            }

            @Override
            public void loadUserData()
            {
            }

            @Override
            public boolean modifyUserData(UserChangeToMake userChangeToMake)
            {
                return false;
            }

            @Override
            public CompositeDisposable getSubscriptions()
            {
                return new CompositeDisposable();
            }

            @Override   // LoadedUserObserver
            public void processBackUserDataLoaded(Usuario usuario)
            {
                assertThat(usuario.getuId(), is(usuarioOrig.getuId()));
                assertThat(usuario.getUserName(), is(USER_PEPE.getUserName()));
                assertThat(usuario.getAlias(), is(USER_PEPE.getAlias()));
            }

            @Override
            public void processBackErrorInReactor(Throwable e)
            {
                // Check modifyUser subscription with wrong password.
                Timber.d("!!!!!!!!!!!!!!!! ERROR !!!!!!!!!!!!!!!!!!");
                UiException ue = (UiException) e;
                assertThat(ue.getErrorBean().getMessage(), is(BAD_REQUEST.getHttpMessage()));
            }

            @Override
            public UserChangeToMake whatDataChangeToMake()
            {
                return null;
            }

            @Override   // ModifyUserObserver
            public void processBackUserModified()
            {
                SpringOauthToken oauthToken2 = tokenCacher.getTokenCache().get();
                assertThat(oauthToken2, notNullValue());
                assertThat(oauthToken2.getValue(), allOf(
                        notNullValue(),
                        not(is(oauthToken1.getValue()))
                ));
            }
        };
    }

}