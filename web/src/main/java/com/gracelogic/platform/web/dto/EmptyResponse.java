package com.gracelogic.platform.web.dto;

/**
 * Author: Igor Parkhomenko
 * Date: 04.10.2015
 * Time: 21:58
 */
public class EmptyResponse extends PlatformResponse {
    private static final EmptyResponse emptyResponse = new EmptyResponse();

    public static EmptyResponse getInstance() {
        return emptyResponse;
    }

    private String value = null;
}
