package com.didekin.incidservice.controller;

import com.didekin.incidservice.domain.Incidencia;
import com.didekin.incidservice.domain.IncidenciaUser;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

import static com.didekin.incidservice.controller.IncidServConstant.*;
import static com.didekin.incidservice.controller.IncidServConstant.GET_INCID_USER_BY_INCID;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 17:05
 */
public interface IncidenciaServEndPoints {

    @DELETE(INCIDENCIA_USER_DELETE + "/{incidenciaId}")
    int deleteIncidencia(@Header("Authorization") String accessToken, @Path("incidenciaId") long incidenciaId);

    @GET(GET_INCID_USER_BY_INCID + "/{incidenciaId}")
    IncidenciaUser getIncidenciaUserWithPowers(@Header("Authorization") String accessToken, @Path("incidenciaId") long incidenciaId);

    @GET(SEE_INCID_BY_COMU + "/{comunidadId}")
    List<Incidencia> incidSeeByComu(@Header("Authorization") String accessToken,
                                    @Path("comunidadId") long comunidadId);

    @GET(SEE_INCID_CLOSED_BY_COMU + "/{comunidadId}")
    List<Incidencia> incidSeeClosedByComu(@Header("Authorization") String accessToken,
                                          @Path("comunidadId") long comunidadId);

    @PUT(MOD_INCID_USER)
    int modifyIncidenciaUser(@Header("Authorization") String accessToken,
                             @Body IncidenciaUser incidenciaUser);

    @PUT(MOD_USER)
    int modifyUser(@Header("Authorization") String accessToken,
                             @Body IncidenciaUser incidenciaUser);

    @POST(REG_INCID_USER)
    int regIncidenciaUser(@Header("Authorization") String accessToken,
                          @Body IncidenciaUser incidenciaUser);

    @POST(REG_USER_IN_INCID)
    int regUserInIncidencia(@Header("Authorization") String accessToken,
                            @Body IncidenciaUser incidenciaUser);
}
