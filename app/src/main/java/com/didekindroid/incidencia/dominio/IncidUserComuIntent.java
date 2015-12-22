package com.didekindroid.incidencia.dominio;

import com.didekin.incidservice.domain.IncidUserComu;
import com.didekindroid.common.utils.SerialNumber;
import com.didekindroid.usuario.dominio.FullUsuarioComuidadIntent;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 16/12/15
 * Time: 12:29
 */
public class IncidUserComuIntent implements Serializable {

    private static final long serialVersionUID = SerialNumber.INCID_USERCOMU_INTENT.number;

    private final transient IncidUserComu incidUserComu;

    public IncidUserComuIntent(final IncidUserComu incidUserComu)
    {
        this.incidUserComu = incidUserComu;
    }

    public IncidUserComu getIncidUserComu()
    {
        return incidUserComu;
    }

    private Object writeReplace()
    {
        return new InnerSerial(incidUserComu);
    }

    private void readObject(ObjectInputStream inputStream) throws InvalidObjectException
    {
        throw new InvalidObjectException("Use innerSerial to serialize");
    }

    private static class InnerSerial implements Serializable {

        private final IncidenciaIntent incidenciaIntent;
        private final FullUsuarioComuidadIntent usuarioComunidadIntent;
        private final short importancia;
        private final Timestamp fechaAlta;

        public InnerSerial(IncidUserComu incidUserComu)
        {
            incidenciaIntent = new IncidenciaIntent(incidUserComu.getIncidencia());
            usuarioComunidadIntent = new FullUsuarioComuidadIntent(incidUserComu.getUsuarioComunidad());
            importancia = incidUserComu.getImportancia();
            fechaAlta = incidUserComu.getFechaAlta();
        }

        private Object readResolve()
        {
            IncidUserComu incidUserComu = new IncidUserComu(
                    incidenciaIntent.getIncidencia(),
                    usuarioComunidadIntent.getUsuarioComunidad(),
                    importancia,
                    fechaAlta);
            return new IncidUserComuIntent(incidUserComu);
        }
    }
}
