package com.gracelogic.platform.dictionary.service;

import com.gracelogic.platform.db.model.IdObject;

import java.util.Collection;

public interface DictionaryService {
    <T extends IdObject> T get(Class<T> clazz, Object id);

    <T> Collection<T> getList(Class<T> clazz);
}
