package com.didekindroid.incidencia.testutils;

import com.didekin.incidservice.dominio.AmbitoIncidencia;
import com.didekin.incidservice.dominio.Avance;
import com.didekin.incidservice.dominio.IncidComment;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.common.activity.UiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.didekindroid.common.utils.UIutils.getStringFromInteger;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 12:37
 */
public final class IncidenciaTestUtils {

    public static final String INCID_DEFAULT_DESC = "Incidencia_1";
    public static final String RESOLUCION_DEFAULT_DESC = "Resolucion_1";
    public static final String AVANCE_DEFAULT_DES = "Avance_1";
    public static final int COSTE_ESTIM_DEFAULT = 1122;
    public static final String COSTE_ESTIM_DEFAULT_String = getStringFromInteger(COSTE_ESTIM_DEFAULT);

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

    public static Resolucion doResolucionAvances(Incidencia incidencia, String descripcion, int costeEstimado, Timestamp fechaPrev)
    {
        Avance avance = new Avance.AvanceBuilder().avanceDesc("").userName(incidencia.getUserName()).build();
        List<Avance> avances = new ArrayList<>(1);
        avances.add(avance);

        return new Resolucion.ResolucionBuilder(incidencia)
                .descripcion(descripcion)
                .costeEstimado(costeEstimado)
                .fechaPrevista(fechaPrev)
                .avances(avances)
                .build();
    }

    public static IncidenciaUser insertGetIncidenciaUser(UsuarioComunidad userComu, int importancia) throws UiException
    {
        IncidImportancia incidImportancia = new IncidImportancia.IncidImportanciaBuilder(
                doIncidencia(userComu.getUsuario().getUserName(), INCID_DEFAULT_DESC, userComu.getComunidad().getC_Id(), (short) 43))
                .usuarioComunidad(userComu)
                .importancia((short) importancia)
                .build();

        IncidenciaServ.regIncidImportancia(incidImportancia);
        return IncidenciaServ.seeIncidsOpenByComu(userComu.getComunidad().getC_Id()).get(0);
    }

    public static IncidenciaUser insertGetIncidImportanciaWithId(long incidenciaId, UsuarioComunidad userComu, int importancia) throws UiException
    {
        IncidImportancia incidImportancia =
                new IncidImportancia.IncidImportanciaBuilder(doIncidenciaWithId(incidenciaId, INCID_DEFAULT_DESC, userComu.getComunidad().getC_Id(), (short) 43))
                .usuarioComunidad(userComu)
                .importancia((short) importancia)
                .build();
        IncidenciaServ.regIncidImportancia(incidImportancia);
        return IncidenciaServ.seeIncidsOpenByComu(userComu.getComunidad().getC_Id()).get(0);
    }

}