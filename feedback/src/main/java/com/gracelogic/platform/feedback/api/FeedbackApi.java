package com.gracelogic.platform.feedback.api;

import com.gracelogic.platform.feedback.service.FeedbackService;
import com.gracelogic.platform.feedback.dto.FeedbackDTO;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.feedback.Path;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;


@Controller
@RequestMapping(value = Path.API_FEEDBACK)
public class FeedbackApi extends AbstractAuthorizedController {

    @Autowired
    private FeedbackService feedbackService;

    @RequestMapping(method = RequestMethod.POST, value = "/add")
    @ResponseBody
    public ResponseEntity addFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        try {
            feedbackDTO.setId(null);
            feedbackService.saveFeedback(feedbackDTO);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse(e.getMessage(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('ORDER:SHOW')")
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity feedbacks(@RequestParam(value = "feedbackTypeId", required = false) UUID feedbackTypeId,
                                 @RequestParam(value = "startDate", required = false) String sStartDate,
                                 @RequestParam(value = "endDate", required = false) String sEndDate,
                                 @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                 @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
                                 @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                 @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        Date startDate = null;
        Date endDate = null;

        try {
            if (!StringUtils.isEmpty(sStartDate)) {
                startDate = TimeUtils.SHORT_DATE_FORMAT.get().parse(sStartDate);
            }
            if (!StringUtils.isEmpty(sEndDate)) {
                endDate = TimeUtils.SHORT_DATE_FORMAT.get().parse(sEndDate);
            }
        } catch (Exception ignored) {
        }

        EntityListResponse<FeedbackDTO> feedbacks = feedbackService.getFeedbacksPaged(feedbackTypeId, startDate, endDate, null, length, null, start, sortField, sortDir);


        return new ResponseEntity<EntityListResponse<FeedbackDTO>>(feedbacks, HttpStatus.OK);
    }
}
