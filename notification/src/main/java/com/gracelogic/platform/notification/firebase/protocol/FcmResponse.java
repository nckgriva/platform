package com.gracelogic.platform.notification.firebase.protocol;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Collections;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FcmResponse {

    private long multicastId;

    private int success;

    private int failure;

    private List<FcmResult> results ;

    public List<FcmResult> getResults() {
        return results == null ? Collections.<FcmResult>emptyList() : results;
    }

    public FcmResponse() {
    }

    public FcmResponse(long multicastId, int success, int failure, List<FcmResult> results) {
        this.multicastId = multicastId;
        this.success = success;
        this.failure = failure;
        this.results = results;
    }

    public long getMulticastId() {
        return multicastId;
    }

    public void setMulticastId(long multicastId) {
        this.multicastId = multicastId;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public void setResults(List<FcmResult> results) {
        this.results = results;
    }
}
