package com.didekindroid.incidencia.testutils;

import com.didekin.incidservice.dominio.AmbitoIncidencia;
import com.didekin.incidservice.dominio.IncidComment;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.common.activity.UiException;

import java.sql.Timestamp;

import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 12:37
 */
public final class IncidenciaTestUtils {

    private IncidenciaTestUtils()
    {
    }

    public static IncidComment doComment(String descComment, Incidencia incidencia)
    {
        Incidencia incidenciaIn = new Incidencia.IncidenciaBuilder()
                .incidenciaId(incidencia.getIncidenciaId())
                .comunidad(new Comunidad.ComunidadBuilder().c_id(incidencia.getComunidad().getC_Id()).build())
                .build();

        return new IncidComment.IncidCommentBuilder().descripcion(descComment)
                .incidencia(incidenciaIn)
                .redactor(null)
                .build();
    }

    public static Incidencia doIncidencia(String userName, String descripcion, long comunidadId, short ambitoId)
    {
        return new Incidencia.IncidenciaBuilder()
                .userName(userName)
                .comunidad(new Comunidad.ComunidadBuilder().c_id(comunidadId).build())
                .descripcion(descripcion)
                .ambitoIncid(new AmbitoIncidencia(ambitoId))
                .build();
    }

    public static Incidencia doIncidenciaWithId(long incidenciaId, String descripcion, long comunidadId, short ambitoId)
    {
        return new Incidencia.IncidenciaBuilder()
                .incidenciaId(incidenciaId)
                .comunidad(new Comunidad.ComunidadBuilder().c_id(comunidadId).build())
                .descripcion(descripcion)
                .ambitoIncid(new AmbitoIncidencia(ambitoId))
                .build();
    }

    public static Resolucion doResolucion(Incidencia incidencia, String descripcion, int costeEstimado, Timestamp fechaPrev)
    {
        return new Resolucion.ResolucionBuilder(incidencia)
                .descripcion(descripcion)
                .costeEstimado(costeEstimado)
                .fechaPrevista(fechaPrev)
                .build();
    }

    public static IncidenciaUser insertGetIncidImportancia(UsuarioComunidad userComu, int importancia) throws UiException
    {
        IncidImportancia incidImportancia = new IncidImportancia.IncidImportanciaBuilder(
                doIncidencia(userComu.getUsuario().getUserName(), "Incidencia One", userComu.getComunidad().getC_Id(), (short) 43))
                .usuarioComunidad(userComu)
                .importancia((short) importancia)
                .build();

        IncidenciaServ.regIncidImportancia(incidImportancia);
        return IncidenciaServ.seeIncidsOpenByComu(userComu.getComunidad().getC_Id()).get(0);
    }

    public static IncidenciaUser insertGetIncidenciaWithId(long incidenciaId, UsuarioComunidad userComu, int importancia) throws UiException
    {
        IncidImportancia incidImportancia = new IncidImportancia.IncidImportanciaBuilder(doIncidenciaWithId(incidenciaId, "descripcion", userComu.getComunidad().getC_Id(), (short) 43))
                .usuarioComunidad(userComu)
                .importancia((short) importancia)
                .build();
        IncidenciaServ.regIncidImportancia(incidImportancia);
        return IncidenciaServ.seeIncidsOpenByComu(userComu.getComunidad().getC_Id()).get(0);
    }
}