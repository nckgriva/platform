package com.gracelogic.platform.user.api;

import com.gracelogic.platform.db.dto.DateFormatConstants;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.user.dto.UserSessionDTO;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.dto.ErrorResponse;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_SESSION)
@Api(value = Path.API_SESSION, tags = {"Session API"},
        authorizations = @Authorization(value = "MybasicAuth"))
public class SessionApi extends AbstractAuthorizedController {

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private UserService userService;

    @ApiOperation(
            value = "getSession",
            notes = "Get list of sessions",
            response =  EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SESSION:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getSessions(  @RequestParam(value = "userId", required = false) UUID userId,
                                        @RequestParam(value = "authIp", required = false) String authIp,
                                        @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
                                        @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
                                        @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                        @RequestParam(value = "calculate", required = false, defaultValue = "false") Boolean calculate,
                                        @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                        @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                        @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                        @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        EntityListResponse<UserSessionDTO> sessions = userService.getSessionsPaged(userId, authIp, startDate, endDate, enrich, calculate, count, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<UserSessionDTO>>(sessions, HttpStatus.OK);

    }
}
