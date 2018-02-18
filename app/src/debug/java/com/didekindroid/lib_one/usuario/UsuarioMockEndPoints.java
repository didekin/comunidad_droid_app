package com.didekindroid.lib_one.usuario;

import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.didekinlib.http.usuario.UsuarioServConstant.OPEN;
import static com.didekinlib.http.usuario.UsuarioServConstant.USER_PARAM;

/**
 * User: pedro@didekin
 * Date: 10/11/2017
 * Time: 17:20
 */

public interface UsuarioMockEndPoints {

    String mockPath = OPEN + "/mock";
    String regComu_User_UserComu = mockPath + "/reg_comu_user_usercomu";
    String user_delete = mockPath + "/user_delete";

    @FormUrlEncoded
    @POST(user_delete)
    Call<Boolean> deleteUser(@Field(USER_PARAM) String userName);

    @POST(regComu_User_UserComu)
    Call<Boolean> regComuAndUserAndUserComu(@Body UsuarioComunidad usuarioCom);
}
