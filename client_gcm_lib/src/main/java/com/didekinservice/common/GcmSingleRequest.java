package com.didekinservice.common;

import com.didekin.common.dominio.BeanBuilder;

/**
 * User: pedro@didekin
 * Date: 02/06/16
 * Time: 13:07
 */
public final class GcmSingleRequest {

    /**
     *  This parameter specifies the recipient of a message.
     *  The value must be a registration token, notification key, or topic.
     */
    final String to;

    final String priority;
    final boolean delay_while_idle;
    final int time_to_live;
    public final String restricted_package_name;
    final String collapse_key;
    final GcmRequestData data;

    public GcmSingleRequest(Builder builder)
    {
        to = builder.to;
        priority = builder.gcmRequest.priority;
        delay_while_idle = builder.gcmRequest.delay_while_idle;
        time_to_live = builder.gcmRequest.time_to_live;
        restricted_package_name = builder.gcmRequest.restricted_package_name;
        collapse_key = builder.gcmRequest.collapse_key;
        data = builder.gcmRequest.data;
    }

    //    ==================== BUILDER ====================

    public static class Builder implements BeanBuilder<GcmSingleRequest>{
        private final String to;
        private final GcmRequest gcmRequest;


        @Override
        public GcmSingleRequest build()
        {
            return new GcmSingleRequest(this);
        }

        public Builder(String to, GcmRequest gcmRequest)
        {
            this.to = to;
            this.gcmRequest = gcmRequest;
        }
    }
}
