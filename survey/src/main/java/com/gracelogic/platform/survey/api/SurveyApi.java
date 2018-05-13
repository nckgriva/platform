package com.gracelogic.platform.survey.api;


import com.gracelogic.platform.survey.Path;
import com.gracelogic.platform.survey.service.SurveyService;
import com.gracelogic.platform.web.dto.ErrorResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(value = Path.API_SURVEY)
@Api(value = Path.API_SURVEY, tags = {"Survey API"})
public class SurveyApi {

    @Autowired
    private SurveyService surveyService;

}
