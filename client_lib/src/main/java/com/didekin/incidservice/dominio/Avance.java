package com.didekin.incidservice.dominio;

import com.didekin.common.BeanBuilder;
import com.didekin.common.dominio.SerialNumber;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Timestamp;

import static com.didekin.common.exception.DidekinExceptionMsg.AVANCE_WRONG_INIT;

/**
 * User: pedro@didekin
 * Date: 11/03/16
 * Time: 12:03
 */
@SuppressWarnings("unused")
public class Avance implements Serializable {

    private final long avanceId;
    private final String avanceDesc;
    private final String userName;
    private final Timestamp fechaAlta;

    private Avance(AvanceBuilder builder)
    {
        avanceId = builder.avanceId;
        avanceDesc = builder.avanceDesc;
        userName = builder.userName;
        fechaAlta = builder.fechaAlta;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Avance avance = (Avance) o;

        if (avanceId > 0L && (avanceId == avance.avanceId)) {
            return true;
        }

        if (userName != null ? !userName.equals(avance.userName) : avance.userName != null) {
            return false;
        }
        return !(fechaAlta != null ? !fechaAlta.equals(avance.fechaAlta) : avance.fechaAlta != null);
    }

    @Override
    public int hashCode()
    {
        if (avanceId > 0L) {
            return ((int) (avanceId ^ (avanceId >>> 32))) * 31;
        }

        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (fechaAlta != null ? fechaAlta.hashCode() : 0);
        return result;
    }

    public long getAvanceId()
    {
        return avanceId;
    }

    public String getAvanceDesc()
    {
        return avanceDesc;
    }

    public String getUserName()
    {
        return userName;
    }

    public Timestamp getFechaAlta()
    {
        return fechaAlta;
    }

    //    ===============================  BUILDER  ============================

    public static final class AvanceBuilder implements BeanBuilder<Avance> {

        private long avanceId;
        private String avanceDesc;
        private String userName;
        private Timestamp fechaAlta;

        public AvanceBuilder()
        {
        }

        public AvanceBuilder avanceId(long initValue)
        {
            avanceId = initValue;
            return this;
        }

        public AvanceBuilder avanceDesc(String initValue)
        {
            avanceDesc = initValue;
            return this;
        }

        public AvanceBuilder userName(String initValue)
        {
            userName = initValue;
            return this;
        }

        public AvanceBuilder fechaAlta(Timestamp initValue)
        {
            fechaAlta = initValue;
            return this;
        }

        public AvanceBuilder copyAvance(Avance initValue)
        {
            avanceId = initValue.avanceId;
            avanceDesc = initValue.avanceDesc;
            userName = initValue.userName;
            fechaAlta = initValue.fechaAlta;
            return this;
        }

        @Override
        public Avance build()
        {
            Avance avance = new Avance(this);
            if (avanceId <= 0L && avance.avanceDesc == null) {
                throw new IllegalStateException(AVANCE_WRONG_INIT.toString());
            }
            return avance;
        }
    }

    //    ============================== SERIALIZATION PROXY ==================================

    /**
     * Return an InnerSerial object that will replace the current Avance instance during serialization.
     * In the deserialization the readResolve() method of the InnerSerial object will be used.
     */
    private Object writeReplace()
    {
        return new InnerSerial(this);
    }

    private void readObject(ObjectInputStream inputStream) throws InvalidObjectException
    {
        throw new InvalidObjectException("Use innerSerial to serialize");
    }

    private static class InnerSerial implements Serializable {

        private static final long serialVersionUID = SerialNumber.INCID_RESOLUCION_AVANCE.number;

        private final long avanceId;
        private final String avanceDesc;
        private final String userName;
        private final Timestamp fechaAlta;


        public InnerSerial(Avance avance)
        {
            avanceId = avance.avanceId;
            avanceDesc = avance.avanceDesc;
            userName = avance.userName;
            fechaAlta = avance.fechaAlta;
        }

        /**
         * Returns a logically equivalent InnerSerial instance of the enclosing class instance,
         * that will replace it during deserialization.
         */
        private Object readResolve()
        {
            return new AvanceBuilder()
                    .avanceId(avanceId)
                    .avanceDesc(avanceDesc)
                    .userName(userName)
                    .fechaAlta(fechaAlta)
                    .build();
        }
    }
}
