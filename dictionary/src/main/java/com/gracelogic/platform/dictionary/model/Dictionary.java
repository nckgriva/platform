package com.gracelogic.platform.dictionary.model;


public interface Dictionary {
    String NAME = "name";
    String CODE = "code";
    String SORT_ORDER = "sort_order";

    Object getId();

    String getName();

    Integer getSortOrder();

    String getCode();
}
