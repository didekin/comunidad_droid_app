package com.didekin.serviceone;

import com.didekin.serviceone.domain.ComunidadIf;
import com.didekin.serviceone.domain.UsuarioComunidadIf;
import com.didekin.serviceone.domain.UsuarioIf;

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

import static com.didekin.serviceone.controller.ControllerConstant.ACCESS_TOKEN_DELETE;
import static com.didekin.serviceone.controller.ControllerConstant.COMUNIDAD_OLDEST_USER;
import static com.didekin.serviceone.controller.ControllerConstant.COMUNIDAD_SEARCH;
import static com.didekin.serviceone.controller.ControllerConstant.COMUS_BY_USER;
import static com.didekin.serviceone.controller.ControllerConstant.LOGIN;
import static com.didekin.serviceone.controller.ControllerConstant.PASSWORD_MODIFY;
import static com.didekin.serviceone.controller.ControllerConstant.PASSWORD_SEND;
import static com.didekin.serviceone.controller.ControllerConstant.REG_COMU_AND_USER_AND_USERCOMU;
import static com.didekin.serviceone.controller.ControllerConstant.REG_COMU_USERCOMU;
import static com.didekin.serviceone.controller.ControllerConstant.REG_USERCOMU;
import static com.didekin.serviceone.controller.ControllerConstant.REG_USER_USERCOMU;
import static com.didekin.serviceone.controller.ControllerConstant.USERCOMUS_BY_COMU;
import static com.didekin.serviceone.controller.ControllerConstant.USERCOMUS_BY_USER;
import static com.didekin.serviceone.controller.ControllerConstant.USERCOMU_DELETE;
import static com.didekin.serviceone.controller.ControllerConstant.USERCOMU_MODIFY;
import static com.didekin.serviceone.controller.ControllerConstant.USERCOMU_READ;
import static com.didekin.serviceone.controller.ControllerConstant.USER_DELETE;
import static com.didekin.serviceone.security.SecurityConstant.COMUNIDAD_READ;
import static com.didekin.serviceone.security.SecurityConstant.COMUNIDAD_WRITE;
import static com.didekin.serviceone.security.SecurityConstant.PSWD_PARAM;
import static com.didekin.serviceone.security.SecurityConstant.USER_PARAM;
import static com.didekin.serviceone.security.SecurityConstant.USER_READ;
import static com.didekin.serviceone.security.SecurityConstant.USER_WRITE;

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
    ComunidadIf getComuData(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @GET(COMUS_BY_USER)
    List<ComunidadIf> getComusByUser(@Header("Authorization") String accessToken);

    @GET(USERCOMU_READ + "/{comunidadId}")
    UsuarioComunidadIf getUserComuByUserAndComu(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @GET(USER_READ)
    UsuarioIf getUserData(@Header("Authorization") String accessToken);

    @GET(COMUNIDAD_OLDEST_USER + "/{comunidadId}")
    boolean isOldestUserComu(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @FormUrlEncoded
    @POST(LOGIN)
    boolean login(@Field(USER_PARAM) String userName, @Field(PSWD_PARAM) String password);

    @PUT(COMUNIDAD_WRITE)
    int modifyComuData(@Header("Authorization") String accessToken, @Body ComunidadIf comunidad);

    @PUT(USER_WRITE)
    int modifyUser(@Header("Authorization") String accessToken, @Body UsuarioIf usuario);

    @PUT(USERCOMU_MODIFY)
    int modifyUserComu(@Header("Authorization") String accessToken, @Body UsuarioComunidadIf usuarioComunidad);

    @FormUrlEncoded
    @POST(PASSWORD_MODIFY)
    int passwordChange(@Header("Authorization") String accessToken, @Field(PSWD_PARAM) String password);

    @FormUrlEncoded
    @POST(PASSWORD_SEND)
    boolean passwordSend(@Field(USER_PARAM) String userName);

    @POST(REG_COMU_AND_USER_AND_USERCOMU)
    boolean regComuAndUserAndUserComu(@Body UsuarioComunidadIf usuarioCom);

    @POST(REG_COMU_USERCOMU)
    boolean regComuAndUserComu(@Header("Authorization") String accessToken,
                               @Body UsuarioComunidadIf usuarioCom);

    @POST(REG_USER_USERCOMU)
    boolean regUserAndUserComu(@Body UsuarioComunidadIf userCom);

    @POST(REG_USERCOMU)
    int regUserComu(@Header("Authorization") String accessToken, @Body UsuarioComunidadIf usuarioComunidad);

    @POST(COMUNIDAD_SEARCH)
    List<ComunidadIf> searchComunidades(@Body ComunidadIf comunidad);

    @GET(USERCOMUS_BY_COMU + "/{comunidadId}")
    List<UsuarioComunidadIf> seeUserComusByComu(@Header("Authorization") String accessToken,
                                              @Path("comunidadId") long comunidadId);

    @GET(USERCOMUS_BY_USER)
    List<UsuarioComunidadIf> seeUserComusByUser(@Header("Authorization") String accessToken);
}