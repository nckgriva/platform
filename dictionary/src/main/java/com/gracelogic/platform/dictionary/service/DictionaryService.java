package com.gracelogic.platform.dictionary.service;

import com.gracelogic.platform.dao.model.IdObject;

/**
 * Author: Igor Parkhomenko
 * Date: 16.03.2016
 * Time: 18:40
 */
public interface DictionaryService {
    <T extends IdObject> T get(Class<T> clazz, Object id);
}
