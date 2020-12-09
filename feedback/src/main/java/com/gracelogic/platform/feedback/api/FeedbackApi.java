package com.gracelogic.platform.feedback.api;

import com.gracelogic.platform.db.dto.DateFormatConstants;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.feedback.model.Feedback;
import com.gracelogic.platform.feedback.service.FeedbackService;
import com.gracelogic.platform.feedback.dto.FeedbackDTO;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.feedback.Path;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.IDResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Controller
@RequestMapping(value = Path.API_FEEDBACK)
public class FeedbackApi extends AbstractAuthorizedController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @RequestMapping(method = RequestMethod.POST, value = "/add")
    @ResponseBody
    public ResponseEntity addFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        try {
            feedbackDTO.setId(null);
            Feedback feedback = feedbackService.saveFeedback(feedbackDTO);
            return new ResponseEntity<IDResponse>(new IDResponse(feedback.getId()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(e.getMessage(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('FEEDBACK:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity feedbacks(@RequestParam(value = "feedbackTypeId", required = false) UUID feedbackTypeId,
                                    @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
                                    @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
                                    @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                    @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
                                    @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                    @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir,
                                    @RequestParam Map<String, String> allRequestParams) {

        Map<String, String> fields = new HashMap<String, String>();
        if (allRequestParams != null) {
            for (String paramName : allRequestParams.keySet()) {
                if (StringUtils.startsWithIgnoreCase(paramName, "fields.")) {
                    String value = null;
                    try {
                        value = URLDecoder.decode(allRequestParams.get(paramName), "UTF-8");
                    } catch (UnsupportedEncodingException ignored) {
                    }

                    if (!StringUtils.isEmpty(value)) {
                        fields.put(paramName.substring("fields.".length()), value);
                    }
                }
            }
        }

        EntityListResponse<FeedbackDTO> feedbacks = feedbackService.getFeedbacksPaged(feedbackTypeId, startDate, endDate, fields, length, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<FeedbackDTO>>(feedbacks, HttpStatus.OK);
    }

    @ApiOperation(
            value = "getFeedback",
            notes = "Get feedback",
            response = FeedbackDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Object not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('FEEDBACK:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getFeedback(@PathVariable(value = "id") UUID id) {
        try {
            FeedbackDTO feedbackDTO = feedbackService.getFeedback(id, getUser());
            return new ResponseEntity<FeedbackDTO>(feedbackDTO, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
