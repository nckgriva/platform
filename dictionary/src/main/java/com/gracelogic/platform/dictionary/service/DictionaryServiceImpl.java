package com.gracelogic.platform.dictionary.service;

import com.gracelogic.platform.dao.model.IdObject;
import com.gracelogic.platform.dao.service.IdObjectService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Igor Parkhomenko
 * Date: 16.03.2016
 * Time: 18:42
 */
@Service
public class DictionaryServiceImpl implements DictionaryService {
    private static Class[] dictionaryClasses = new Class[] {
            
    };

    private static Logger logger = Logger.getLogger(DictionaryServiceImpl.class);

    @Autowired
    private IdObjectService idObjectService;

    private final HashMap<Class, HashMap<Object, IdObject>> dictionaries = new HashMap<Class, HashMap<Object, IdObject>>();

    @PostConstruct
    private void init() {
        for (Class clazz : dictionaryClasses) {
            final HashMap<Object, IdObject> map = new HashMap<Object, IdObject>();
            List list = idObjectService.getList(clazz);
            for (Object o : list) {
                IdObject idObject = (IdObject) o;
                map.put(idObject.getId(), idObject);
            }
            dictionaries.put(clazz, map);
        }

        logger.info(String.format("Loaded %d dictionaries", dictionaries.size()));
    }


    @Override
    public <T extends IdObject> T get(Class<T> clazz, Object id) {
        if (dictionaries.containsKey(clazz)) {
            HashMap<Object, IdObject> dictionary = dictionaries.get(clazz);
            if (dictionary.containsKey(id)) {
                return (T) dictionary.get(id);
            }
        }
        else {
            logger.error(String.format("Dictionary for class %s is not found", clazz.getSimpleName()));
        }
        return null;
    }

}
