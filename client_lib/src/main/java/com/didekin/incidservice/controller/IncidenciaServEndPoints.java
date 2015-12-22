package com.didekin.incidservice.controller;

import com.didekin.incidservice.domain.IncidUserComu;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 17:05
 */
public interface IncidenciaServEndPoints {

    @POST(IncidServConstant.REG_INCID_USERCOMU)
    int regIncidenciaUserComu(@Header("Authorization") String accessToken,
                              @Body IncidUserComu incidUserComu);

    @GET(IncidServConstant.SEE_INCID_BY_USER)
    List<IncidUserComu> incidSeeByUser(@Header("Authorization") String accessToken);
}
