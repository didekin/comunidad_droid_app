package com.didekinservice.common.gcm;

import com.didekin.common.controller.RetrofitHandler;
import com.didekin.common.exception.ErrorBean;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * User: pedro@didekin
 * Date: 31/05/16
 * Time: 17:47
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class GcmEndPointImp implements GcmEndPoint {

    private final RetrofitHandler retrofitHandler;

    public GcmEndPointImp(RetrofitHandler retrofitHandler)
    {
        this.retrofitHandler = retrofitHandler;
    }

    @Override
    public Call<GcmResponse> sendGcmSingleRequest(GcmSingleRequest singleRequest)
    {
        return retrofitHandler.getService(GcmEndPoint.class).sendGcmSingleRequest(singleRequest);
    }

    @Override
    public Call<GcmResponse> sendGcmMulticastRequest(GcmMulticastRequest multicastRequest)
    {
        return retrofitHandler.getService(GcmEndPoint.class).sendGcmMulticastRequest(multicastRequest);
    }

    @Override
    public Call<GcmResponse> sendGcmMulticastRequest(String acceptEncoding, GcmMulticastRequest multicastRequest)
    {
        return retrofitHandler.getService(GcmEndPoint.class).sendGcmMulticastRequest(acceptEncoding, multicastRequest);
    }

//    ================================ CONVENIENCE METHODS ================================

    public GcmResponse sendGcmMulticastRequestImp(GcmMulticastRequest multicastRequest)
            throws GcmException
    {
        GcmResponse gcmResponse;
        try {
            Response<GcmResponse> response = sendGcmMulticastRequest(multicastRequest).execute();
            if (!response.isSuccessful()) {
                throw new GcmException(retrofitHandler.getErrorBean(response));
            }
            gcmResponse = response.body();
            gcmResponse.setTokensToProcess(multicastRequest.registration_ids);
        } catch (IOException e) {
            throw new GcmException(new ErrorBean(e.getMessage(), GcmResponse.GcmErrorMessage.InternalServerError.httpStatusCode));
        }
        return gcmResponse;
    }
}
