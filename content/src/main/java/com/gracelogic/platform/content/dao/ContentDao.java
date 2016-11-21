package com.gracelogic.platform.content.dao;


import com.gracelogic.platform.content.model.Element;
import com.gracelogic.platform.user.model.AuthCode;
import com.gracelogic.platform.user.model.IncorrectLoginAttempt;
import com.gracelogic.platform.user.model.User;

import java.util.*;

/**
 * Author: Igor Parkhomenko
 * Date: 20.07.13
 * Time: 17:10
 */
public interface ContentDao {
    Integer getElementsCount(UUID sectionId, Boolean active, Date validOnDate, Map<String, String> fields);

    List<Element> getElements(UUID sectionId, Boolean active, Date validOnDate, Map<String, String> fields, String sortField, String sortDir, Integer startRecord, Integer recordsOnPage);
}
