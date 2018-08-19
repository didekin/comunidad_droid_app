package com.didekindroid.incidencia.testutils;

import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.AmbitoIncidencia;
import com.didekinlib.model.incidencia.dominio.Avance;
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.lib_one.util.UiUtil.getMilliSecondsFromCalendarAdd;
import static com.didekindroid.lib_one.util.UiUtil.getStringFromInteger;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static java.util.Calendar.SECOND;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 12:37
 */
public final class IncidTestData {

    public static final String INCID_DEFAULT_DESC = "Incidencia_1";
    public static final String RESOLUCION_DEFAULT_DESC = "Resolucion_1";
    public static final String AVANCE_DEFAULT_DES = "Avance_1";
    public static final int COSTE_ESTIM_DEFAULT = 1122;
    static final String COSTE_ESTIM_DEFAULT_String = getStringFromInteger(COSTE_ESTIM_DEFAULT);
    private static final short importancia_default = (short) 3;

    private IncidTestData()
    {
    }

    public static IncidImportancia insertGetIncidImportancia(UsuarioComunidad userComu) throws Exception
    {
        regComuUserUserComuGetAuthTk(userComu);
        UsuarioComunidad userComuDb = userComuDao.seeUserComusByUser().blockingGet().get(0);
        return insertGetIncidImportancia(userComuDb, importancia_default);
    }

    public static IncidImportancia insertGetIncidImportancia(UsuarioComunidad userComu, short importancia)
    {
        // UsuarioComunidad already in DB.
        IncidImportancia incidImportancia = new IncidImportancia.IncidImportanciaBuilder(
                doIncidencia(userComu.getUsuario().getUserName(), INCID_DEFAULT_DESC, userComu.getComunidad().getC_Id(), (short) 43))
                .usuarioComunidad(userComu)
                .importancia(importancia)
                .build();

        incidenciaDao.regIncidImportancia(incidImportancia).blockingGet();
        Incidencia incidenciaDb = incidenciaDao.seeIncidsOpenByComu(userComu.getComunidad().getC_Id()).blockingGet().get(0).getIncidencia();
        return incidenciaDao.seeIncidImportanciaRaw(incidenciaDb.getIncidenciaId()).blockingGet().getIncidImportancia();
    }

    public static IncidenciaUser insertGetIncidenciaUser(UsuarioComunidad userComu, int importancia)
    {
        // UsuarioComunidad already in DB.
        IncidImportancia incidImportancia =
                new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(
                                userComu.getUsuario().getUserName(),
                                INCID_DEFAULT_DESC,
                                userComu.getComunidad().getC_Id(),
                                (short) 43))
                        .usuarioComunidad(userComu)
                        .importancia((short) importancia)
                        .build();

        incidenciaDao.regIncidImportancia(incidImportancia).blockingGet();
        return incidenciaDao.seeIncidsOpenByComu(userComu.getComunidad().getC_Id()).blockingGet().get(0);
    }

    public static IncidenciaUser insertGetIncidenciaUser(long incidenciaId, UsuarioComunidad userComu, int importancia)
    {
        // UsuarioComunidad already in DB.
        IncidImportancia incidImportancia =
                new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(incidenciaId, userComu))
                        .usuarioComunidad(userComu)
                        .importancia((short) importancia)
                        .build();

        incidenciaDao.regIncidImportancia(incidImportancia).blockingGet();
        return incidenciaDao.seeIncidsOpenByComu(userComu.getComunidad().getC_Id()).blockingGet().get(0);
    }

    public static Resolucion insertGetResolucionNoAdvances(IncidImportancia incidImportancia)
    {
        // Precondition: incidImportancia already in DB.
        // Registramos resolución.
        Resolucion resolucion = doResolucion(
                incidImportancia.getIncidencia(),
                RESOLUCION_DEFAULT_DESC,
                COSTE_ESTIM_DEFAULT,
                new Timestamp(getMilliSecondsFromCalendarAdd(SECOND, 30)));
        incidenciaDao.regResolucion(resolucion).blockingGet();
        return incidenciaDao.seeResolucionRaw(resolucion.getIncidencia().getIncidenciaId()).blockingGet();
    }

    public static Resolucion insertGetResolucionAdvances(IncidImportancia incidImportancia)
    {
        // Precondition: incidImportancia already in DB.
        // Registramos resolución.
        Resolucion resolucion = insertGetResolucionNoAdvances(incidImportancia);
        // Modificamos con avances.
        Avance avance = new Avance.AvanceBuilder().avanceDesc("avance1_desc").build();
        List<Avance> avances = new ArrayList<>(1);
        avances.add(avance);
        resolucion = new Resolucion.ResolucionBuilder(incidImportancia.getIncidencia())
                .copyResolucion(resolucion)
                .avances(avances)
                .build();
        assertThat(incidenciaDao.modifyResolucion(resolucion).blockingGet(), is(2));
        return incidenciaDao.seeResolucionRaw(resolucion.getIncidencia().getIncidenciaId()).blockingGet();
    }

    public static Resolucion insertGetDefaultResolucion(UsuarioComunidad userComu)
    {
        // Precondition: usuarioComunidad already in DB.
        // Insertamos resolución.
        Incidencia incidencia = insertGetIncidenciaUser(userComu, 1).getIncidencia();
        Resolucion resolucion = doResolucion(
                incidencia,
                RESOLUCION_DEFAULT_DESC,
                1122,
                new Timestamp(getMilliSecondsFromCalendarAdd(SECOND, 2)));
        assertThat(incidenciaDao.regResolucion(resolucion).blockingGet(), is(1));
        return incidenciaDao.seeResolucionRaw(resolucion.getIncidencia().getIncidenciaId()).blockingGet();
    }

    public static Incidencia doIncidencia(String userName, String descripcion, long comunidadId, short ambitoId)
    {
        // Precondition: incidencia not in DB.
        return new Incidencia.IncidenciaBuilder()
                .userName(userName)
                .comunidad(new Comunidad.ComunidadBuilder().c_id(comunidadId).build())
                .descripcion(descripcion)
                .ambitoIncid(new AmbitoIncidencia(ambitoId))
                .build();
    }

    public static Incidencia doIncidencia(String userName, String descripcion, long comunidadId, long indidenciaId,short ambitoId)
    {
        // Precondition: incidencia in DB.
        return new Incidencia.IncidenciaBuilder()
                .userName(userName)
                .comunidad(new Comunidad.ComunidadBuilder().c_id(comunidadId).build())
                .incidenciaId(indidenciaId)
                .descripcion(descripcion)
                .ambitoIncid(new AmbitoIncidencia(ambitoId))
                .build();
    }

    public static Incidencia doIncidencia(long incidenciaId, UsuarioComunidad userComu)
    {
        // Precondition: incidencia and usuarioComunidad in DB.
        return new Incidencia.IncidenciaBuilder()
                .incidenciaId(incidenciaId)
                .comunidad(new Comunidad.ComunidadBuilder()
                        .c_id(userComu.getComunidad().getC_Id())
                        .build())
                .descripcion(INCID_DEFAULT_DESC)
                .ambitoIncid(new AmbitoIncidencia((short) 43))
                .build();
    }

    public static List<IncidenciaUser> doIncidenciaUsers(IncidImportancia incidImportancia)
    {
        final List<IncidenciaUser> list = new ArrayList<>(3);
        Timestamp resolucionDate = incidImportancia.getIncidencia().getFechaCierre();
        Timestamp altaIncidDate = incidImportancia.getIncidencia().getFechaAlta();
        IncidenciaUser iu_1 = doSimpleIncidenciaUser(33L, altaIncidDate, 34L, resolucionDate);
        IncidenciaUser iu_2 = doSimpleIncidenciaUser(11L, altaIncidDate, 14L, resolucionDate);
        IncidenciaUser iu_3 = doSimpleIncidenciaUser(22L, altaIncidDate, 24L, resolucionDate);
        list.add(iu_1);
        list.add(iu_2);
        list.add(iu_3);
        return list;
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

    public static Resolucion doResolucion(Incidencia incidencia, String descripcion, int costeEstimado, Timestamp fechaPrev)
    {
        return new Resolucion.ResolucionBuilder(incidencia)
                .descripcion(descripcion)
                .costeEstimado(costeEstimado)
                .fechaPrevista(fechaPrev)
                .build();
    }

    // ================================  Private methods ===================================

    public static IncidenciaUser doSimpleIncidenciaUser(long incidenciaId, Timestamp incidAltaDate, long usuarioId, Timestamp resolucionDate)
    {
        final Incidencia incidencia = doSimpleIncidenciaWithId(incidenciaId, incidAltaDate, resolucionDate);
        return new IncidenciaUser.IncidenciaUserBuilder(incidencia)
                .usuario(new Usuario.UsuarioBuilder()
                        .uId(usuarioId)
                        .userName(incidencia.getUserName())
                        .build())
                .fechaAltaResolucion(resolucionDate)
                .build();
    }

    private static Incidencia doSimpleIncidenciaWithId(long incidenciaId, Timestamp altaDate, Timestamp resolucionDate)
    {
        return new Incidencia.IncidenciaBuilder()
                .incidenciaId(incidenciaId)
                .comunidad(new Comunidad.ComunidadBuilder().c_id(987L).build())
                .descripcion("Simple description")
                .userName("simpleUser")
                .ambitoIncid(new AmbitoIncidencia((short) 33))
                .fechaAlta(altaDate)
                .fechaCierre(resolucionDate)
                .build();
    }
}