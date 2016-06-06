package com.didekinservice.common;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static com.didekin.common.controller.CommonServiceConstant.MIME_JSON;

/**
 * User: pedro@didekin
 * Date: 29/11/15
 * Time: 12:56
 */
@SuppressWarnings("unused")
public interface GcmEndPoint {

    // Firebase gcm URL.
    String FCM_HOST_PORT = "https://fcm.googleapis.com";
    // Path added to base URL.
    String FCM_PATH_REQUEST = "/fcm/send";
    //

    // Accept-encoding header.
    String ACCEPT_ENCODING = "Accept-Encoding";
    String ACCEPT_ENCODING_IDENTITY = "identity";
    String ACCEPT_ENCODING_GZIP = "gzip";

    // Authorization header.
    String AUTHORIZATION_KEY = "Authorization:key";
    String GCM_API_KEY = "AIzaSyBRcUJ9Z9LNlmblvEBuX53U1jFTkwRcr4g";

    @Headers({
            "Content-Type:" + MIME_JSON,
            "Authorization:key= " + GCM_API_KEY
    })
    @POST(FCM_PATH_REQUEST)
    Call<GcmResponse> sendGcmSingleRequest(@Body GcmSingleRequest singleRequest);

    @Headers({
            "Content-Type:" + MIME_JSON,
            "Authorization:key= " + GCM_API_KEY
    })
    @POST(FCM_PATH_REQUEST)
    Call<GcmResponse> sendGcmMulticastRequest(@Body GcmMulticastRequest multicastRequest);

    /**
     *  Overloaded version for tests: accept-encoding=identity.
     */
    @Headers({
            "Content-Type:" + MIME_JSON,
            "Authorization:key= " + GCM_API_KEY
    })
    @POST(FCM_PATH_REQUEST)
    Call<GcmResponse> sendGcmMulticastRequest(@Header(ACCEPT_ENCODING) String acceptEncoding,
                                              @Body GcmMulticastRequest multicastRequest);
}
