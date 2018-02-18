package com.didekindroid.usuariocomunidad;

import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static com.didekinlib.http.usuario.UsuarioServConstant.OPEN;

/**
 * User: pedro@didekin
 * Date: 10/11/2017
 * Time: 17:20
 */

@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface UserComuMockEndPoints {

    String regUser_UserComu = OPEN + "/mock/reg_user_usercomu";

    @POST(regUser_UserComu)
    Call<Boolean> regUserAndUserComu(@Body UsuarioComunidad userCom);
}
