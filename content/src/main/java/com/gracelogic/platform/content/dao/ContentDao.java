package com.gracelogic.platform.content.dao;


import com.gracelogic.platform.content.model.Element;

import java.util.*;

public interface ContentDao {
    Integer getElementsCount(Collection<UUID> sectionIds, Boolean active, Date validOnDate, Map<String, String> fields);

    List<Element> getElements(Collection<UUID> sectionIds, Boolean active, Date validOnDate, Map<String, String> fields, String sortField, String sortDir, Integer startRecord, Integer recordsOnPage);
}
