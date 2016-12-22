package com.gracelogic.platform.dictionary.api;


import com.gracelogic.platform.dictionary.Path;
import com.gracelogic.platform.dictionary.dto.DictionaryDTO;
import com.gracelogic.platform.dictionary.model.Dictionary;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.web.dto.ErrorResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Author: Igor Parkhomenko
 * Date: 16.07.2016
 * Time: 21:58
 */
@Controller
@RequestMapping(value = Path.API_DICTIONARY)
@Api(value = Path.API_DICTIONARY, description = "Контроллер для получения данных системных справочников")
public class DictionaryApi {

    @Autowired
    private DictionaryService dictionaryService;

    @ApiOperation(
            value = "getDictionary",
            notes = "Получить содержание справочника"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Something exceptional happened")})
    @RequestMapping(value = "/{className:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getDictionary(@ApiParam(name = "className", value = "className")
                                        @PathVariable(value = "className") String className) {
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        }
        catch (ClassNotFoundException ignored) {}

        if (clazz != null) {
            Collection<Dictionary> dictionaries = dictionaryService.getList(clazz);
            if (dictionaries != null) {
                List<DictionaryDTO> dtos = new LinkedList<>();
                for (Dictionary d : dictionaries) {
                    dtos.add(DictionaryDTO.prepare(d));
                }

                Collections.sort(dtos, new Comparator<DictionaryDTO>() {
                    @Override
                    public int compare(final DictionaryDTO o1, final DictionaryDTO o2) {
                        return o1 == null ? 1 : o2 == null ? -1 : o1.getSortOrder().compareTo(o2.getSortOrder());
                    }
                });

                return new ResponseEntity<List<DictionaryDTO>>(dtos, HttpStatus.OK);
            }
        }

        return new ResponseEntity<ErrorResponse>(new ErrorResponse("NOT_FOUND", "Dictionary not found"), HttpStatus.BAD_REQUEST);
    }
}
