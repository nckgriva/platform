package com.gracelogic.platform.dictionary.service;

import com.gracelogic.platform.db.model.IdObject;

import java.util.Collection;

/**
 * Author: Igor Parkhomenko
 * Date: 16.03.2016
 * Time: 18:40
 */
public interface DictionaryService {
    <T extends IdObject> T get(Class<T> clazz, Object id);

    <T> Collection<T> getList(Class<T> clazz);
}
