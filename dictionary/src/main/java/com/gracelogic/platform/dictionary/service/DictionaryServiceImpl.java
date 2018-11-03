package com.gracelogic.platform.dictionary.service;

import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.db.service.IdObjectService;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DictionaryServiceImpl implements DictionaryService {
    private static Logger logger = Logger.getLogger(DictionaryServiceImpl.class);

    @Autowired
    private IdObjectService idObjectService;

    private class DictionaryKey {
        private Class clazz;
        private Object id;

        public Class getClazz() {
            return clazz;
        }

        public void setClazz(Class clazz) {
            this.clazz = clazz;
        }

        public Object getId() {
            return id;
        }

        public void setId(Object id) {
            this.id = id;
        }

        DictionaryKey(Class clazz, Object id) {
            this.clazz = clazz;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DictionaryKey)) return false;

            DictionaryKey that = (DictionaryKey) o;

            if (!getClazz().equals(that.getClazz())) return false;
            return getId().equals(that.getId());
        }

        @Override
        public int hashCode() {
            int result = getClazz().hashCode();
            result = 31 * result + getId().hashCode();
            return result;
        }
    }

    private Map<DictionaryKey, IdObject> cache = ExpiringMap.builder()
//            .expiration(30, TimeUnit.SECONDS)
            .entryLoader(key -> idObjectService.getObjectById(((DictionaryKey) key).getClazz(), ((DictionaryKey) key).getId()))
            .build();


    @Override
    public <T extends IdObject> T get(Class<T> clazz, Object id) {
        return (T) cache.get(new DictionaryKey(clazz, id));
    }

    @Override
    public <T> List<T> getList(Class<T> clazz) {
        return idObjectService.getList(clazz, null, null, null, "el.sortOrder", null, null);
    }

}
