package com.didekin.incidservice.controller;

import com.didekin.incidservice.dominio.ImportanciaUser;
import com.didekin.incidservice.dominio.IncidAndResolBundle;
import com.didekin.incidservice.dominio.IncidComment;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import static com.didekin.incidservice.controller.IncidServConstant.CLOSE_INCIDENCIA;
import static com.didekin.incidservice.controller.IncidServConstant.DELETE_INCID;
import static com.didekin.incidservice.controller.IncidServConstant.MOD_INCID_IMPORTANCIA;
import static com.didekin.incidservice.controller.IncidServConstant.MOD_RESOLUCION;
import static com.didekin.incidservice.controller.IncidServConstant.REG_INCID_COMMENT;
import static com.didekin.incidservice.controller.IncidServConstant.REG_INCID_IMPORTANCIA;
import static com.didekin.incidservice.controller.IncidServConstant.REG_RESOLUCION;
import static com.didekin.incidservice.controller.IncidServConstant.SEE_INCIDS_CLOSED_BY_COMU;
import static com.didekin.incidservice.controller.IncidServConstant.SEE_INCIDS_OPEN_BY_COMU;
import static com.didekin.incidservice.controller.IncidServConstant.SEE_INCID_COMMENTS;
import static com.didekin.incidservice.controller.IncidServConstant.SEE_INCID_IMPORTANCIA;
import static com.didekin.incidservice.controller.IncidServConstant.SEE_RESOLUCION;
import static com.didekin.incidservice.controller.IncidServConstant.SEE_USERCOMUS_IMPORTANCIA;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 17:05
 */
public interface IncidenciaServEndPoints {

    @PUT(CLOSE_INCIDENCIA)
    Call<Integer> closeIncidencia(@Header("Authorization") String accessToken,
                                  @Body Resolucion resolucion);

    @DELETE(DELETE_INCID + "/{incidenciaId}")
    Call<Integer> deleteIncidencia(@Header("Authorization") String accessToken, @Path("incidenciaId") long incidenciaId);

    @PUT(MOD_INCID_IMPORTANCIA)
    Call<Integer> modifyIncidImportancia(@Header("Authorization") String accessToken,
                                         @Body IncidImportancia incidImportancia);

    @PUT(MOD_RESOLUCION)
    Call<Integer> modifyResolucion(@Header("Authorization") String accessToken,
                                   @Body Resolucion resolucion);

    @POST(REG_INCID_COMMENT)
    Call<Integer> regIncidComment(@Header("Authorization") String accessToken,
                                  @Body IncidComment comment);

    @POST(REG_INCID_IMPORTANCIA)
    Call<Integer> regIncidImportancia(@Header("Authorization") String accessToken,
                                      @Body IncidImportancia incidImportancia);

    @POST(REG_RESOLUCION)
    Call<Integer> regResolucion(@Header("Authorization") String accessToken,
                                @Body Resolucion resolucion);

    @GET(SEE_INCID_IMPORTANCIA + "/{incidenciaId}")
    Call<IncidAndResolBundle> seeIncidImportancia(@Header("Authorization") String accessToken, @Path("incidenciaId") long incidenciaId);

    @GET(SEE_INCID_COMMENTS + "/{incidenciaId}")
    Call<List<IncidComment>> seeCommentsByIncid(@Header("Authorization") String accessToken,
                                          @Path("incidenciaId") long incidenciaId);

    @GET(SEE_INCIDS_CLOSED_BY_COMU + "/{comunidadId}")
    Call<List<IncidenciaUser>> seeIncidsClosedByComu(@Header("Authorization") String accessToken,
                                               @Path("comunidadId") long comunidadId);

    @GET(SEE_INCIDS_OPEN_BY_COMU + "/{comunidadId}")
    Call<List<IncidenciaUser>> seeIncidsOpenByComu(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId);

    @GET(SEE_RESOLUCION + "/{resolucionId}")
    Call<Resolucion> seeResolucion(@Header("Authorization") String accessToken, @Path("resolucionId") long resolucionId);

    @GET(SEE_USERCOMUS_IMPORTANCIA + "/{incidenciaId}")
    Call<List<ImportanciaUser>> seeUserComusImportancia(@Header("Authorization") String accessToken,
                                                  @Path("incidenciaId") long incidenciaId);
}
