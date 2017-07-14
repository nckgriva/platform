package com.gracelogic.platform.dictionary.model;

import java.util.UUID;

public interface Dictionary {
    String NAME = "name";
    String CODE = "code";
    String SORT_ORDER = "sort_order";

    UUID getId();

    String getName();

    Integer getSortOrder();

    String getCode();
}
