package com.didekin.serviceone.controller;

import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

import static com.didekin.common.oauth2.OauthConstant.COMUNIDAD_READ;
import static com.didekin.common.oauth2.OauthConstant.COMUNIDAD_WRITE;
import static com.didekin.common.oauth2.OauthConstant.PSWD_PARAM;
import static com.didekin.common.oauth2.OauthConstant.USER_PARAM;
import static com.didekin.common.oauth2.OauthConstant.USER_READ;
import static com.didekin.common.oauth2.OauthConstant.USER_WRITE;
import static com.didekin.serviceone.controller.ServOneConstant.ACCESS_TOKEN_DELETE;
import static com.didekin.serviceone.controller.ServOneConstant.COMUNIDAD_OLDEST_USER;
import static com.didekin.serviceone.controller.ServOneConstant.COMUNIDAD_SEARCH;
import static com.didekin.serviceone.controller.ServOneConstant.COMUS_BY_USER;
import static com.didekin.serviceone.controller.ServOneConstant.LOGIN;
import static com.didekin.serviceone.controller.ServOneConstant.PASSWORD_MODIFY;
import static com.didekin.serviceone.controller.ServOneConstant.PASSWORD_SEND;
import static com.didekin.serviceone.controller.ServOneConstant.REG_COMU_AND_USER_AND_USERCOMU;
import static com.didekin.serviceone.controller.ServOneConstant.REG_COMU_USERCOMU;
import static com.didekin.serviceone.controller.ServOneConstant.REG_USERCOMU;
import static com.didekin.serviceone.controller.ServOneConstant.REG_USER_USERCOMU;
import static com.didekin.serviceone.controller.ServOneConstant.USERCOMUS_BY_COMU;
import static com.didekin.serviceone.controller.ServOneConstant.USERCOMUS_BY_USER;
import static com.didekin.serviceone.controller.ServOneConstant.USERCOMU_DELETE;
import static com.didekin.serviceone.controller.ServOneConstant.USERCOMU_MODIFY;
import static com.didekin.serviceone.controller.ServOneConstant.USERCOMU_READ;
import static com.didekin.serviceone.controller.ServOneConstant.USER_DELETE;

/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 14:13
 */
public interface ServiceOneEndPoints {


    @DELETE(ACCESS_TOKEN_DELETE + "/{oldTk}")
    boolean deleteAccessToken(@Header("Authorization") String accessToken, @Path("oldTk") String oldAccessToken);

    @DELETE(USER_DELETE)
    boolean deleteUser(@Header("Authorization") String accessToken);

    @DELETE(USERCOMU_DELETE + "/{comunidadId}")
    int deleteUserComu(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @GET(COMUNIDAD_READ + "/{comunidadId}")
    Comunidad getComuData(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @GET(COMUS_BY_USER)
    List<Comunidad> getComusByUser(@Header("Authorization") String accessToken);

    @GET(USERCOMU_READ + "/{comunidadId}")
    UsuarioComunidad getUserComuByUserAndComu(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @GET(USER_READ)
    Usuario getUserData(@Header("Authorization") String accessToken);

    @GET(COMUNIDAD_OLDEST_USER + "/{comunidadId}")
    boolean isOldestUserComu(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @FormUrlEncoded
    @POST(LOGIN)
    boolean login(@Field(USER_PARAM) String userName, @Field(PSWD_PARAM) String password);

    @PUT(COMUNIDAD_WRITE)
    int modifyComuData(@Header("Authorization") String accessToken, @Body Comunidad comunidad);

    @PUT(USER_WRITE)
    int modifyUser(@Header("Authorization") String accessToken, @Body Usuario usuario);

    @PUT(USERCOMU_MODIFY)
    int modifyUserComu(@Header("Authorization") String accessToken, @Body UsuarioComunidad usuarioComunidad);

    @FormUrlEncoded
    @POST(PASSWORD_MODIFY)
    int passwordChange(@Header("Authorization") String accessToken, @Field(PSWD_PARAM) String password);

    @FormUrlEncoded
    @POST(PASSWORD_SEND)
    boolean passwordSend(@Field(USER_PARAM) String userName);

    @POST(REG_COMU_AND_USER_AND_USERCOMU)
    boolean regComuAndUserAndUserComu(@Body UsuarioComunidad usuarioCom);

    @POST(REG_COMU_USERCOMU)
    boolean regComuAndUserComu(@Header("Authorization") String accessToken,
                               @Body UsuarioComunidad usuarioCom);

    @POST(REG_USER_USERCOMU)
    boolean regUserAndUserComu(@Body UsuarioComunidad userCom);

    @POST(REG_USERCOMU)
    int regUserComu(@Header("Authorization") String accessToken, @Body UsuarioComunidad usuarioComunidad);

    @POST(COMUNIDAD_SEARCH)
    List<Comunidad> searchComunidades(@Body Comunidad comunidad);

    @GET(USERCOMUS_BY_COMU + "/{comunidadId}")
    List<UsuarioComunidad> seeUserComusByComu(@Header("Authorization") String accessToken,
                                              @Path("comunidadId") long comunidadId);

    @GET(USERCOMUS_BY_USER)
    List<UsuarioComunidad> seeUserComusByUser(@Header("Authorization") String accessToken);
}