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
    String SORT_ORDER = "sort_order";

    UUID getId();

    String getName();

    Integer getSortOrder();

    String getCode();
}
