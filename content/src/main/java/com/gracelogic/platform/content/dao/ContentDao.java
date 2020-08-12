package com.gracelogic.platform.content.dao;


import com.gracelogic.platform.content.model.Element;

import java.util.*;

public interface ContentDao {
    Integer getElementsCount(String query, Collection<String> queryFields, Collection<String> sectionIds, Boolean active, Date validOnDate, Map<String, String> fields);

    List<Element> getElements(String query, Collection<String> queryFields, Collection<String> sectionIds, Boolean active, Date validOnDate, Map<String, String> fields, String sortField, String sortDir, Integer startRecord, Integer recordsOnPage);
}
