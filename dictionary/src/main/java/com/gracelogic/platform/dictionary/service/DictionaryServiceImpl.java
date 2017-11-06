package com.gracelogic.platform.dictionary.service;

import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.model.Dictionary;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public class DictionaryServiceImpl implements DictionaryService {
    private static Set<Class<? extends Dictionary>> dictionaryClasses;

    private static Logger logger = Logger.getLogger(DictionaryServiceImpl.class);

    @Autowired
    private IdObjectService idObjectService;

    private final HashMap<Class, HashMap<Object, IdObject>> dictionaries = new HashMap<Class, HashMap<Object, IdObject>>();

    @PostConstruct
    private void init() {
        dictionaryClasses = new Reflections("com.gracelogic").getSubTypesOf(Dictionary.class);

        for (Class clazz : dictionaryClasses) {
            try {
                final HashMap<Object, IdObject> map = new HashMap<Object, IdObject>();
                List list = idObjectService.getList(clazz);
                for (Object o : list) {
                    IdObject idObject = (IdObject) o;
                    map.put(idObject.getId(), idObject);
                }
                dictionaries.put(clazz, map);
            }
            catch (Exception e) {
                logger.warn("Failed to load dictionary: " + clazz.getCanonicalName());
            }
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

    @Override
    public <T> Collection<T> getList(Class<T> clazz) {
        if (dictionaries.containsKey(clazz)) {
            HashMap<Object, IdObject> dictionary = dictionaries.get(clazz);
            return (Collection<T>) dictionaries.get(clazz).values();
        }
        else {
            logger.error(String.format("Dictionary for class %s is not found", clazz.getSimpleName()));
        }
        return null;
    }

}
