package com.didekindroid.usuariocomunidad.repository;

import com.didekindroid.lib_one.api.HttpInitializerIf;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.AuthTkCacherIf;
import com.didekindroid.lib_one.security.SecInitializerIf;
import com.didekindroid.lib_one.usuario.dao.AppIdHelper;
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

/**
 * User: pedro@didekin
 * Date: 21/11/16
 * Time: 19:15
 */
public final class UserComuDao implements UsuarioComunidadEndPoints {

    public static final UserComuDao userComuDao = new UserComuDao(secInitializer.get(), httpInitializer.get());
    private final UsuarioComunidadEndPoints endPoint;
    private final AuthTkCacherIf tkCacher;
    private final AppIdHelper idHelper;
    private final Function<Boolean, CompletableSource> userDataErrorFunc;

    public UserComuDao(SecInitializerIf secInitializerIn, HttpInitializerIf httpInitializerIn)
    {
        endPoint = httpInitializerIn.getHttpHandler().getService(UsuarioComunidadEndPoints.class);
        tkCacher = secInitializerIn.getTkCacher();
        idHelper = appIdSingle;
        userDataErrorFunc = isUpdated -> isUpdated ? complete() : error(new UiException(new ErrorBean(USER_DATA_NOT_INSERTED)));
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
        return idHelper.getTokenSingle()
                .flatMap(gcmToken -> deleteUserComu(tkCacher.doAuthHeaderStr(gcmToken), comunidadId))
                .flatMap(getResponseSingleFunction())
                .doOnSuccess(rowsUpdated -> {
                    if (rowsUpdated == IS_USER_DELETED) {
                        tkCacher.updateAuthToken(null);
                    }
                })
                .doOnError(uiExceptionConsumer);
    }

    public Single<List<Comunidad>> getComusByUser()
    {
        Timber.d("getComusByUser()");
        return idHelper.getTokenSingle()
                .flatMap(gcmToken -> getComusByUser(tkCacher.doAuthHeaderStr(gcmToken)))
                .flatMap(getRespSingleListFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Maybe<UsuarioComunidad> getUserComuByUserAndComu(long comunidadId)
    {
        Timber.d("getUserComuByUserAndComu()");
        return idHelper.getTokenSingle()
                .flatMapMaybe(gcmTk -> getUserComuByUserAndComu(tkCacher.doAuthHeaderStr(gcmTk), comunidadId))
                .flatMap(getResponseMaybeFunction());
    }

    public Single<Boolean> isOldestOrAdmonUserComu(long comunidadId)
    {
        Timber.d("isOldestOrAdmonUserComu()");
        return idHelper.getTokenSingle()
                .flatMap(gcmTk ->
                        isOldestOrAdmonUserComu(tkCacher.doAuthHeaderStr(gcmTk), comunidadId))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<Integer> modifyComuData(Comunidad comunidad)
    {
        Timber.d("modifyComuData()");
        return idHelper.getTokenSingle()
                .flatMap(gcmTk ->
                        modifyComuData(tkCacher.doAuthHeaderStr(gcmTk), comunidad))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<Integer> modifyUserComu(UsuarioComunidad userComu)
    {
        Timber.d("modifyUserComu()");
        return idHelper.getTokenSingle()
                .flatMap(gcmTk -> modifyUserComu(tkCacher.doAuthHeaderStr(gcmTk), userComu))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Completable regComuAndUserAndUserComu(UsuarioComunidad usuarioCom)
    {
        Timber.d(("regComuAndUserAndUserComu()"));
        return idHelper.getTokenSingle()
                .map(gcmTk ->
                        new UsuarioComunidad.UserComuBuilder(
                                usuarioCom.getComunidad(),
                                new Usuario.UsuarioBuilder().copyUsuario(usuarioCom.getUsuario()).gcmToken(gcmTk).build()
                        ).userComuRest(usuarioCom).build()
                )
                .flatMap(userComuGcmTk -> regComuAndUserAndUserComu(getDeviceLanguage(), userComuGcmTk))
                .flatMap(getResponseSingleFunction())
                .flatMapCompletable(userDataErrorFunc)
                .doOnError(uiExceptionConsumer);
    }

    public Completable regComuAndUserComu(UsuarioComunidad usuarioComunidad)
    {
        Timber.d("regComuAndUserComu()");
        return idHelper.getTokenSingle()
                .flatMap(gcmTk -> regComuAndUserComu(tkCacher.doAuthHeaderStr(gcmTk), usuarioComunidad))
                .flatMap(getResponseSingleFunction())
                .flatMapCompletable(
                        isUpdated -> isUpdated ? complete() : error(new UiException(new ErrorBean(USER_DATA_NOT_INSERTED)))
                )
                .doOnError(uiExceptionConsumer);
    }

    public Completable regUserAndUserComu(UsuarioComunidad userCom)
    {
        Timber.d("regUserAndUserComu()");
        return idHelper.getTokenSingle()
                .map(gcmTk ->
                        new UsuarioComunidad.UserComuBuilder(
                                userCom.getComunidad(),
                                new Usuario.UsuarioBuilder().copyUsuario(userCom.getUsuario()).gcmToken(gcmTk).build()
                        ).userComuRest(userCom).build()
                )
                .flatMap(userComIn -> regUserAndUserComu(getDeviceLanguage(), userComIn))
                .flatMap(getResponseSingleFunction())
                .flatMapCompletable(userDataErrorFunc)
                .doOnError(uiExceptionConsumer);
    }

    public Completable regUserComu(UsuarioComunidad usuarioComunidad)
    {
        Timber.d("regUserComu()");
        return idHelper.getTokenSingle()
                .flatMap(gcmTk -> regUserComu(tkCacher.doAuthHeaderStr(gcmTk), usuarioComunidad))
                .flatMap(getResponseSingleFunction())
                .flatMapCompletable(
                        rowInserted -> (rowInserted > 0) ? complete() : error(new UiException(new ErrorBean(USER_DATA_NOT_INSERTED)))
                )
                .doOnError(uiExceptionConsumer);
    }

    public Single<List<UsuarioComunidad>> seeUserComusByComu(long idComunidad)
    {
        Timber.d("seeUserComusByComu()");
        return idHelper.getTokenSingle()
                .flatMap(gcmTk ->
                        seeUserComusByComu(tkCacher.doAuthHeaderStr(gcmTk), idComunidad))
                .flatMap(getRespSingleListFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<List<UsuarioComunidad>> seeUserComusByUser()
    {
        Timber.d("seeUserComusByUser()");
        return idHelper.getTokenSingle()
                .flatMap(gcmTk -> seeUserComusByUser(tkCacher.doAuthHeaderStr(gcmTk)))
                .flatMap(getRespSingleListFunction())
                .doOnError(uiExceptionConsumer);
    }
}
