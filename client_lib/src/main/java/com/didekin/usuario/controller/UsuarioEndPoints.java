package com.didekin.usuario.controller;

import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import static com.didekin.common.controller.CommonServiceConstant.MIME_JSON;
import static com.didekin.usuario.controller.UsuarioServiceConstant.ACCESS_TOKEN_DELETE;
import static com.didekin.usuario.controller.UsuarioServiceConstant.COMUNIDAD_OLDEST_USER;
import static com.didekin.usuario.controller.UsuarioServiceConstant.COMUNIDAD_READ;
import static com.didekin.usuario.controller.UsuarioServiceConstant.COMUNIDAD_SEARCH;
import static com.didekin.usuario.controller.UsuarioServiceConstant.COMUNIDAD_WRITE;
import static com.didekin.usuario.controller.UsuarioServiceConstant.COMUS_BY_USER;
import static com.didekin.usuario.controller.UsuarioServiceConstant.GCM_TOKEN_PARAM;
import static com.didekin.usuario.controller.UsuarioServiceConstant.LOGIN;
import static com.didekin.usuario.controller.UsuarioServiceConstant.PASSWORD_MODIFY;
import static com.didekin.usuario.controller.UsuarioServiceConstant.PASSWORD_SEND;
import static com.didekin.usuario.controller.UsuarioServiceConstant.PSWD_PARAM;
import static com.didekin.usuario.controller.UsuarioServiceConstant.REG_COMU_AND_USER_AND_USERCOMU;
import static com.didekin.usuario.controller.UsuarioServiceConstant.REG_COMU_USERCOMU;
import static com.didekin.usuario.controller.UsuarioServiceConstant.REG_USERCOMU;
import static com.didekin.usuario.controller.UsuarioServiceConstant.REG_USER_USERCOMU;
import static com.didekin.usuario.controller.UsuarioServiceConstant.USERCOMUS_BY_COMU;
import static com.didekin.usuario.controller.UsuarioServiceConstant.USERCOMUS_BY_USER;
import static com.didekin.usuario.controller.UsuarioServiceConstant.USERCOMU_DELETE;
import static com.didekin.usuario.controller.UsuarioServiceConstant.USERCOMU_MODIFY;
import static com.didekin.usuario.controller.UsuarioServiceConstant.USERCOMU_READ;
import static com.didekin.usuario.controller.UsuarioServiceConstant.USER_DELETE;
import static com.didekin.usuario.controller.UsuarioServiceConstant.USER_PARAM;
import static com.didekin.usuario.controller.UsuarioServiceConstant.USER_READ;
import static com.didekin.usuario.controller.UsuarioServiceConstant.USER_READ_GCM_TOKEN;
import static com.didekin.usuario.controller.UsuarioServiceConstant.USER_WRITE;
import static com.didekin.usuario.controller.UsuarioServiceConstant.USER_WRITE_GCM_TOKEN;

/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 14:13
 */
public interface UsuarioEndPoints {

    @DELETE(ACCESS_TOKEN_DELETE + "/{oldTk}")
    Call<Boolean> deleteAccessToken(@Header("Authorization") String accessToken, @Path("oldTk") String oldAccessToken);

    @DELETE(USER_DELETE)
    Call<Boolean> deleteUser(@Header("Authorization") String accessToken);

    @DELETE(USERCOMU_DELETE + "/{comunidadId}")
    Call<Integer> deleteUserComu(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @GET(COMUNIDAD_READ + "/{comunidadId}")
    Call<Comunidad> getComuData(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @GET(COMUS_BY_USER)
    Call<List<Comunidad>> getComusByUser(@Header("Authorization") String accessToken);

    @Headers({
            "Content-Type:" + MIME_JSON
    })
    @GET(USER_READ_GCM_TOKEN)
    Call<GcmTokenWrapper> getGcmToken(@Header("Authorization") String accessToken);

    @GET(USERCOMU_READ + "/{comunidadId}")
    Call<UsuarioComunidad> getUserComuByUserAndComu(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @GET(USER_READ)
    Call<Usuario> getUserData(@Header("Authorization") String accessToken);

    @GET(COMUNIDAD_OLDEST_USER + "/{comunidadId}")
    Call<Boolean> isOldestOrAdmonUserComu(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @FormUrlEncoded
    @POST(LOGIN)
    Call<Boolean> login(@Field(USER_PARAM) String userName, @Field(PSWD_PARAM) String password);

    @PUT(COMUNIDAD_WRITE)
    Call<Integer> modifyComuData(@Header("Authorization") String accessToken, @Body Comunidad comunidad);

    @FormUrlEncoded
    @POST(USER_WRITE_GCM_TOKEN)
    Call<Integer> modifyUserGcmToken(@Header("Authorization") String accessToken, @Field(GCM_TOKEN_PARAM) String gcmToken);

    @PUT(USER_WRITE)
    Call<Integer> modifyUser(@Header("Authorization") String accessToken, @Body Usuario usuario);

    @PUT(USERCOMU_MODIFY)
    Call<Integer> modifyUserComu(@Header("Authorization") String accessToken, @Body UsuarioComunidad usuarioComunidad);

    @FormUrlEncoded
    @POST(PASSWORD_MODIFY)
    Call<Integer> passwordChange(@Header("Authorization") String accessToken, @Field(PSWD_PARAM) String password);

    @FormUrlEncoded
    @POST(PASSWORD_SEND)
    Call<Boolean> passwordSend(@Field(USER_PARAM) String userName);

    @POST(REG_COMU_AND_USER_AND_USERCOMU)
    Call<Boolean> regComuAndUserAndUserComu(@Body UsuarioComunidad usuarioCom);

    @POST(REG_COMU_USERCOMU)
    Call<Boolean> regComuAndUserComu(@Header("Authorization") String accessToken,
                                     @Body UsuarioComunidad usuarioCom);

    @POST(REG_USER_USERCOMU)
    Call<Boolean> regUserAndUserComu(@Body UsuarioComunidad userCom);

    @POST(REG_USERCOMU)
    Call<Integer> regUserComu(@Header("Authorization") String accessToken, @Body UsuarioComunidad usuarioComunidad);

    @POST(COMUNIDAD_SEARCH)
    Call<List<Comunidad>> searchComunidades(@Body Comunidad comunidad);

    @GET(USERCOMUS_BY_COMU + "/{comunidadId}")
    Call<List<UsuarioComunidad>> seeUserComusByComu(@Header("Authorization") String accessToken,
                                                    @Path("comunidadId") long comunidadId);

    @GET(USERCOMUS_BY_USER)
    Call<List<UsuarioComunidad>> seeUserComusByUser(@Header("Authorization") String accessToken);

}