package com.gracelogic.platform.dictionary.api;


import com.gracelogic.platform.dictionary.Path;
import com.gracelogic.platform.dictionary.dto.DictionaryDTO;
import com.gracelogic.platform.dictionary.model.Dictionary;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.web.dto.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(value = Path.API_DICTIONARY)
public class DictionaryApi {

    @Autowired
    private DictionaryService dictionaryService;

    @RequestMapping(value = "/{className:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getDictionary(@PathVariable(value = "className") String className) {
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        }
        catch (ClassNotFoundException ignored) {}

        if (clazz != null) {
            Collection<Dictionary> dictionaries = dictionaryService.getList(clazz);
            if (dictionaries != null) {
                List<DictionaryDTO> DTOs = new LinkedList<>();
                for (Dictionary d : dictionaries) {
                    DTOs.add(DictionaryDTO.prepare(d));
                }

                return new ResponseEntity<List<DictionaryDTO>>(DTOs, HttpStatus.OK);
            }
        }

        return new ResponseEntity<ErrorResponse>(new ErrorResponse("NOT_FOUND", "Dictionary not found"), HttpStatus.BAD_REQUEST);
    }
}
