package com.didekinservice.incidservice.gcm;

import com.didekin.common.controller.RetrofitHandler;
import com.didekin.common.exception.ErrorBean;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;

import static com.didekinservice.incidservice.gcm.GcmResponse.GcmErrorMessage.InternalServerError;

/**
 * User: pedro@didekin
 * Date: 31/05/16
 * Time: 17:47
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class IncidGcmEndPointImp implements IncidGcmEndPoint {

    private final RetrofitHandler retrofitHandler;
    private final IncidGcmEndPoint endPoint;
    private final String acceptEncoding;

    public IncidGcmEndPointImp(RetrofitHandler retrofitHandler, String acceptEncoding)
    {
        this.retrofitHandler = retrofitHandler;
        endPoint = retrofitHandler.getService(IncidGcmEndPoint.class);
        this.acceptEncoding = acceptEncoding;
    }

    @Override
    public Call<GcmResponse> sendGcmIncidRequest(@Header("Accept-Encoding") String acceptEncoding, @Body GcmIncidRequest.Single singleRequest)
    {
        return endPoint.sendGcmIncidRequest(acceptEncoding, singleRequest);
    }

    @Override
    public Call<GcmResponse> sendGcmMulticastIncidRequest(@Header("Accept-Encoding") String acceptEncoding, @Body GcmIncidRequest.Multicast multicastRequest)
    {
        return endPoint.sendGcmMulticastIncidRequest(acceptEncoding, multicastRequest);
    }

//    ================================ CONVENIENCE METHODS ================================

    public GcmResponse sendGcmIncidRequest(GcmIncidRequest.Single singleRequest)
    {
        GcmResponse gcmResponse = null;
        try {
            gcmResponse = sendGcmIncidRequest(acceptEncoding, singleRequest).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gcmResponse;
    }

    public GcmResponse sendGcmMulticastIncidRequest(GcmIncidRequest.Multicast multicastRequest) throws GcmException
    {
        GcmResponse gcmResponse;
        try {
            Response<GcmResponse> response = sendGcmMulticastIncidRequest(acceptEncoding, multicastRequest).execute();
            if (!response.isSuccessful()) {
                throw new GcmException(retrofitHandler.getErrorBean(response));
            }
            gcmResponse = response.body();
        } catch (IOException e) {
            throw new GcmException(new ErrorBean(e.getMessage(), InternalServerError.httpStatusCode));
        }
        return gcmResponse;
    }
}
