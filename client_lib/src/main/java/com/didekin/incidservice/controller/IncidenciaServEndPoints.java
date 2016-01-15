package com.didekin.incidservice.controller;

import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekin.incidservice.domain.Incidencia;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 17:05
 */
public interface IncidenciaServEndPoints {

    @POST(IncidServConstant.REG_INCID_USERCOMU)
    int regIncidenciaUser(@Header("Authorization") String accessToken,
                          @Body IncidenciaUser incidenciaUser);

    @GET(IncidServConstant.SEE_INCID_BY_COMU + "/{comunidadId}")
    List<Incidencia> incidSeeByComu(@Header("Authorization") String accessToken,
                                       @Path("comunidadId") long comunidadId);

    @GET(IncidServConstant.SEE_INCID_CLOSED_BY_COMU + "/{comunidadId}")
    List<Incidencia> incidSeeClosedByComu(@Header("Authorization") String accessToken,
                                             @Path("comunidadId") long comunidadId);
}
