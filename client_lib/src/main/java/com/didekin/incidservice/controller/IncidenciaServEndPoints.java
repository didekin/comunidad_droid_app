package com.didekin.incidservice.controller;

import com.didekin.incidservice.dominio.IncidComment;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

import static com.didekin.incidservice.controller.IncidServConstant.DELETE_INCID;
import static com.didekin.incidservice.controller.IncidServConstant.MOD_INCID_IMPORTANCIA;
import static com.didekin.incidservice.controller.IncidServConstant.MOD_RESOLUCION;
import static com.didekin.incidservice.controller.IncidServConstant.REG_INCID_COMMENT;
import static com.didekin.incidservice.controller.IncidServConstant.REG_INCID_IMPORTANCIA;
import static com.didekin.incidservice.controller.IncidServConstant.REG_RESOLUCION;
import static com.didekin.incidservice.controller.IncidServConstant.SEE_INCID_IMPORTANCIA;
import static com.didekin.incidservice.controller.IncidServConstant.SEE_INCID_CLOSED_BY_COMU;
import static com.didekin.incidservice.controller.IncidServConstant.SEE_INCID_COMMENTS;
import static com.didekin.incidservice.controller.IncidServConstant.SEE_INCID_OPEN_BY_COMU;
import static com.didekin.incidservice.controller.IncidServConstant.SEE_RESOLUCION;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 17:05
 */
public interface IncidenciaServEndPoints {

    @DELETE(DELETE_INCID + "/{incidenciaId}")
    int deleteIncidencia(@Header("Authorization") String accessToken, @Path("incidenciaId") long incidenciaId);

    @PUT(MOD_INCID_IMPORTANCIA)
    int modifyIncidImportancia(@Header("Authorization") String accessToken,
                               @Body IncidImportancia incidImportancia);

    @PUT(MOD_RESOLUCION)
    int modifyResolucion(@Header("Authorization") String accessToken,
                               @Body Resolucion resolucion);

    @POST(REG_INCID_COMMENT)
    int regIncidComment(@Header("Authorization") String accessToken,
                        @Body IncidComment comment);

    @POST(REG_INCID_IMPORTANCIA)
    int regIncidImportancia(@Header("Authorization") String accessToken,
                            @Body IncidImportancia incidImportancia);

    @POST(REG_RESOLUCION)
    int regResolucion(@Header("Authorization") String accessToken,
                      @Body Resolucion resolucion);

    @GET(SEE_INCID_IMPORTANCIA + "/{incidenciaId}")
    IncidImportancia seeIncidImportancia(@Header("Authorization") String accessToken, @Path("incidenciaId") long incidenciaId);

    @GET(SEE_INCID_COMMENTS + "/{incidenciaId}")
    List<IncidComment> seeCommentsByIncid(@Header("Authorization") String accessToken,
                                          @Path("incidenciaId") long incidenciaId);

    @GET(SEE_INCID_OPEN_BY_COMU + "/{comunidadId}")
    List<IncidenciaUser> seeIncidsOpenByComu(@Header("Authorization") String accessToken,
                                             @Path("comunidadId") long comunidadId);

    @GET(SEE_INCID_CLOSED_BY_COMU + "/{comunidadId}")
    List<IncidenciaUser> seeIncidsClosedByComu(@Header("Authorization") String accessToken,
                                               @Path("comunidadId") long comunidadId);

    @GET(SEE_RESOLUCION + "/{resolucionId}")
    Resolucion seeResolucion(@Header("Authorization") String accessToken, @Path("resolucionId") long resolucionId);
}
