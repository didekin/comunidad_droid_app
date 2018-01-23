package com.didekindroid.usuariocomunidad.testutil;

import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.didekinlib.http.UsuarioServConstant.OPEN;
import static com.didekinlib.http.UsuarioServConstant.USER_PARAM;

/**
 * User: pedro@didekin
 * Date: 10/11/2017
 * Time: 17:20
 */

public interface UserComuMockEndPoints {

    String mockPath = OPEN + "/mock";
    String regComu_User_UserComu = mockPath + "/reg_comu_user_usercomu";
    String regUser_UserComu = mockPath + "/reg_user_usercomu";
    String user_delete = mockPath + "/user_delete";

    @FormUrlEncoded
    @POST(user_delete)
    Call<Boolean> deleteUser(@Field(USER_PARAM) String userName);

    @POST(regComu_User_UserComu)
    Call<Boolean> regComuAndUserAndUserComu(@Body UsuarioComunidad usuarioCom);

    @POST(regUser_UserComu)
    Call<Boolean> regUserAndUserComu(@Body UsuarioComunidad userCom);
}
