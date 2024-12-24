package com.gracelogic.platform.suggestion.api;


import com.gracelogic.platform.suggestion.Path;
import com.gracelogic.platform.suggestion.dto.SuggestedVariant;
import com.gracelogic.platform.suggestion.exception.SuggestionProcessorNotFoundException;
import com.gracelogic.platform.suggestion.service.SuggestionService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.web.dto.ErrorResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping(value = Path.API_SUGGESTION)
public class SuggestionApi extends AbstractAuthorizedController {

    @Autowired
    private SuggestionService suggestionService;

    @RequestMapping(value = "/{processorName}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getSuggestedVariants(@PathVariable(value = "processorName") String processorName,
                                               @RequestParam(value = "query", required = true) String query,
                                               @RequestParam(value = "flags", required = false) String sFlags,
                                               HttpServletRequest request) {
        List<String> flags = new LinkedList<>();
        if (!StringUtils.isEmpty(sFlags)) {
            for (String s : sFlags.split(",")) {
                flags.add(StringUtils.upperCase(StringUtils.trim(s)));
            }
        }

        try {
            List<SuggestedVariant> variants = suggestionService.process(processorName, query, flags, getUser(), request.getParameterMap());
            return new ResponseEntity<List<SuggestedVariant>>(variants, HttpStatus.OK);
        } catch (SuggestionProcessorNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("PROCESSOR_NOT_FOUND", "Suggestion processor not found"), HttpStatus.BAD_REQUEST);
        }
    }
}
