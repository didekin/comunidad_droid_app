package com.didekindroid.usuariocomunidad.repository;

import com.didekindroid.lib_one.api.HttpInitializerIf;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.AuthTkCacherIf;
import com.didekindroid.lib_one.security.SecInitializerIf;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.http.usuariocomunidad.UsuarioComunidadEndPoints;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.api.exception.UiException.uiExceptionConsumer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.usuario.dao.AppIdHelper.appIdSingle;
import static com.didekindroid.lib_one.util.Device.getDeviceLanguage;
import static com.didekindroid.lib_one.util.RxJavaUtil.getRespSingleListFunction;
import static com.didekindroid.lib_one.util.RxJavaUtil.getResponseMaybeFunction;
import static com.didekindroid.lib_one.util.RxJavaUtil.getResponseSingleFunction;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_DATA_NOT_INSERTED;
import static com.didekinlib.http.usuario.UsuarioServConstant.IS_USER_DELETED;
import static io.reactivex.Completable.complete;
import static io.reactivex.Completable.error;
import static io.reactivex.Completable.fromAction;
import static io.reactivex.Single.just;

/**
 * User: pedro@didekin
 * Date: 21/11/16
 * Time: 19:15
 */
public final class UserComuDao implements UsuarioComunidadEndPoints {

    public static final UserComuDao userComuDao = new UserComuDao(secInitializer.get(), httpInitializer.get());
    private final UsuarioComunidadEndPoints endPoint;
    private final AuthTkCacherIf tkCacher;
    private final Function<Boolean, CompletableSource> userDataErrorFunc;

    public UserComuDao(SecInitializerIf secInitializerIn, HttpInitializerIf httpInitializerIn)
    {
        endPoint = httpInitializerIn.getHttpHandler().getService(UsuarioComunidadEndPoints.class);
        tkCacher = secInitializerIn.getTkCacher();
        userDataErrorFunc = isUpdated ->
                isUpdated ?
                        fromAction(() -> tkCacher.updateIsRegistered(true)) :
                        error(new UiException(new ErrorBean(USER_DATA_NOT_INSERTED)));
    }

    public AuthTkCacherIf getTkCacher()
    {
        return tkCacher;
    }

    //  ================================== UserComuEndPoints implementation ============================

    @Override
    public Single<Response<Integer>> deleteUserComu(String authHeader, long comunidadId)
    {
        return endPoint.deleteUserComu(authHeader, comunidadId);
    }

    @Override
    public Single<Response<List<Comunidad>>> getComusByUser(String authHeader)
    {
        return endPoint.getComusByUser(authHeader);
    }

    @Override
    public Maybe<Response<UsuarioComunidad>> getUserComuByUserAndComu(String authHeader, long comunidadId)
    {
        return endPoint.getUserComuByUserAndComu(authHeader, comunidadId);
    }

    @Override
    public Single<Response<Boolean>> isOldestOrAdmonUserComu(String authHeader, long comunidadId)
    {
        return endPoint.isOldestOrAdmonUserComu(authHeader, comunidadId);
    }

    @Override
    public Single<Response<Integer>> modifyComuData(String currentauthHeader, Comunidad comunidad)
    {
        return endPoint.modifyComuData(currentauthHeader, comunidad);
    }

    @Override
    public Single<Response<Integer>> modifyUserComu(String authHeader, UsuarioComunidad userComu)
    {
        return endPoint.modifyUserComu(authHeader, userComu);
    }

    @Override
    public Single<Response<Boolean>> regComuAndUserAndUserComu(String localeToStr, UsuarioComunidad usuarioCom)
    {
        return endPoint.regComuAndUserAndUserComu(localeToStr, usuarioCom);
    }

    @Override
    public Single<Response<Boolean>> regComuAndUserComu(String authHeader, UsuarioComunidad usuarioCom)
    {
        return endPoint.regComuAndUserComu(authHeader, usuarioCom);
    }

    @Override
    public Single<Response<Boolean>> regUserAndUserComu(String localeToStr, UsuarioComunidad userCom)
    {
        return endPoint.regUserAndUserComu(localeToStr, userCom);
    }

    @Override
    public Single<Response<Integer>> regUserComu(String authHeader, UsuarioComunidad usuarioComunidad)
    {
        return endPoint.regUserComu(authHeader, usuarioComunidad);
    }

    @Override
    public Single<Response<List<UsuarioComunidad>>> seeUserComusByComu(String authHeader, long comunidadId)
    {
        return endPoint.seeUserComusByComu(authHeader, comunidadId);
    }

    @Override
    public Single<Response<List<UsuarioComunidad>>> seeUserComusByUser(String authHeader)
    {
        return endPoint.seeUserComusByUser(authHeader);
    }

//  =============================================================================
//                          CONVENIENCE METHODS
//  =============================================================================

    public Single<Integer> deleteUserComu(long comunidadId)
    {
        Timber.d("deleteUserComu()");
        return just(comunidadId)
                .flatMap(comunidadIdIn -> deleteUserComu(tkCacher.doAuthHeaderStr(), comunidadIdIn))
                .flatMap(getResponseSingleFunction())
                .doOnSuccess(rowsUpdated -> {
                    if (rowsUpdated == IS_USER_DELETED) {
                        tkCacher.updateIsRegistered(false);
                    }
                })
                .doOnError(uiExceptionConsumer);
    }

    public Single<List<Comunidad>> getComusByUser()
    {
        Timber.d("getComusByUser()");
        return just(true)
                .flatMap(booleanIn -> getComusByUser(tkCacher.doAuthHeaderStr()))
                .flatMap(getRespSingleListFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Maybe<UsuarioComunidad> getUserComuByUserAndComu(long comunidadId)
    {
        Timber.d("getUserComuByUserAndComu()");
        return Maybe.just(comunidadId)
                .flatMap(comunidadIdIn -> getUserComuByUserAndComu(tkCacher.doAuthHeaderStr(), comunidadIdIn))
                .flatMap(getResponseMaybeFunction());
    }

    public Single<Boolean> isOldestOrAdmonUserComu(long comunidadId)
    {
        Timber.d("isOldestOrAdmonUserComu()");
        return just(comunidadId)
                .flatMap(comunidadIdIn ->
                        isOldestOrAdmonUserComu(tkCacher.doAuthHeaderStr(), comunidadIdIn))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<Integer> modifyComuData(Comunidad comunidad)
    {
        Timber.d("modifyComuData()");
        return just(comunidad)
                .flatMap(comunidadIn ->
                        modifyComuData(tkCacher.doAuthHeaderStr(), comunidadIn))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<Integer> modifyUserComu(UsuarioComunidad userComu)
    {
        Timber.d("modifyUserComu()");
        return just(userComu)
                .flatMap(userComuIn ->
                        modifyUserComu(tkCacher.doAuthHeaderStr(), userComuIn))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Completable regComuAndUserAndUserComu(UsuarioComunidad usuarioCom)
    {
        Timber.d(("regComuAndUserAndUserComu()"));
        return just(getUserWithAppTk(usuarioCom))
                .flatMap(usuarioComIn -> regComuAndUserAndUserComu(getDeviceLanguage(), usuarioComIn))
                .flatMap(getResponseSingleFunction())
                .flatMapCompletable(userDataErrorFunc)
                .doOnError(uiExceptionConsumer);
    }

    public Completable regComuAndUserComu(UsuarioComunidad usuarioComunidad)
    {
        Timber.d("regComuAndUserComu()");
        return just(usuarioComunidad)
                .flatMap(usuarioComunidadIn -> regComuAndUserComu(tkCacher.doAuthHeaderStr(), usuarioComunidadIn))
                .flatMap(getResponseSingleFunction())
                .flatMapCompletable(
                        isUpdated -> isUpdated ? complete() : error(new UiException(new ErrorBean(USER_DATA_NOT_INSERTED)))
                )
                .doOnError(uiExceptionConsumer);
    }

    public Completable regUserAndUserComu(UsuarioComunidad userCom)
    {
        Timber.d("regUserAndUserComu()");
        return just(getUserWithAppTk(userCom))
                .flatMap(userComIn -> regUserAndUserComu(getDeviceLanguage(), userComIn))
                .flatMap(getResponseSingleFunction())
                .flatMapCompletable(userDataErrorFunc)
                .doOnError(uiExceptionConsumer);
    }

    public Completable regUserComu(UsuarioComunidad usuarioComunidad)
    {
        Timber.d("regUserComu()");
        return just(usuarioComunidad)
                .flatMap(usuarioComunidadIn -> regUserComu(tkCacher.doAuthHeaderStr(), usuarioComunidadIn))
                .flatMap(getResponseSingleFunction())
                .flatMapCompletable(
                        rowInserted -> (rowInserted > 0) ? complete() : error(new UiException(new ErrorBean(USER_DATA_NOT_INSERTED)))
                )
                .doOnError(uiExceptionConsumer);
    }

    public Single<List<UsuarioComunidad>> seeUserComusByComu(long idComunidad)
    {
        Timber.d("seeUserComusByComu()");
        return just(idComunidad)
                .flatMap(idComunidadIn ->
                        seeUserComusByComu(tkCacher.doAuthHeaderStr(), idComunidad))
                .flatMap(getRespSingleListFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<List<UsuarioComunidad>> seeUserComusByUser()
    {
        Timber.d("seeUserComusByUser()");
        return just(true)
                .flatMap(aBoolean -> seeUserComusByUser(tkCacher.doAuthHeaderStr()))
                .flatMap(getRespSingleListFunction())
                .doOnError(uiExceptionConsumer);
    }

    // ============================  Helpers =============================

    /**
     * This method should be called asynchronously.
     */
    UsuarioComunidad getUserWithAppTk(UsuarioComunidad usuarioCom)
    {
        return new UsuarioComunidad.UserComuBuilder(
                usuarioCom.getComunidad(),
                new Usuario.UsuarioBuilder().copyUsuario(usuarioCom.getUsuario()).gcmToken(appIdSingle.getTokenSingle().blockingGet()).build()
        ).userComuRest(usuarioCom).build();
    }


}
