package com.gracelogic.platform.dictionary.model;

import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 11.12.14
 * Time: 12:36
 */
public interface Dictionary {
    String NAME = "name";
    String CODE = "code";

    String getName();

    UUID getId();
}
