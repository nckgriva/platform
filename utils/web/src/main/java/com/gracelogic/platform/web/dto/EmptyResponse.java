package com.gracelogic.platform.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include= JsonSerialize.Inclusion.ALWAYS)
public class EmptyResponse extends PlatformResponse {
    private static final EmptyResponse emptyResponse = new EmptyResponse();

    public static EmptyResponse getInstance() {
        return emptyResponse;
    }

    private String value = null;
}
