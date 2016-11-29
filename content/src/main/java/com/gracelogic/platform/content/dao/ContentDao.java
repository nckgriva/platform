package com.gracelogic.platform.content.dao;


import com.gracelogic.platform.content.model.Element;

import java.util.*;

/**
 * Author: Igor Parkhomenko
 * Date: 20.07.13
 * Time: 17:10
 */
public interface ContentDao {
    Integer getElementsCount(Collection<UUID> sectionIds, Boolean active, Date validOnDate, Map<String, String> fields);

    List<Element> getElements(Collection<UUID> sectionIds, Boolean active, Date validOnDate, Map<String, String> fields, String sortField, String sortDir, Integer startRecord, Integer recordsOnPage);
}
