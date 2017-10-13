package com.gracelogic.platform.user.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.user.dto.RoleDTO;
import com.gracelogic.platform.user.model.Role;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.IDResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_ROLE)
@Api(value = Path.API_ROLE, tags = {"Role API"},
        authorizations = @Authorization(value = "MybasicAuth"))
public class RoleApi extends AbstractAuthorizedController {
    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private UserService userService;

    @ApiOperation(
            value = "getRoles",
            notes = "Get list of roles, fetchGrants must be true",
            response =  EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Something exceptional happened", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('ROLE:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getRoles(@RequestParam(value = "code", required = false) String code,
                                   @RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "grantId", required = false) UUID grantId,
                                   @RequestParam(value = "fetchGrants", required = false, defaultValue = "false") Boolean fetchGrants,
                                   @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                   @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                   @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                   @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        EntityListResponse<RoleDTO> roles = userService.getRolesPaged(code, name, grantId != null ? Collections.singletonList(grantId) : null, fetchGrants, count, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<RoleDTO>>(roles, HttpStatus.OK);
    }

    @ApiOperation(
            value = "getRole",
            notes = "Get role",
            response = RoleDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Something exceptional happened")})
    @PreAuthorize("hasAuthority('ROLE:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getRole(@PathVariable(value = "id") UUID id,
                                  @RequestParam(value = "fetchGrants", required = false, defaultValue = "false") Boolean fetchGrants) {
        try {
            RoleDTO roleDTO = userService.getRole(id, fetchGrants);
            return new ResponseEntity<RoleDTO>(roleDTO, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", messageSource.getMessage("db.NOT_FOUND", null, getUserLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "saveRole",
            notes = "Save role",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Something exceptional happened")})
    @PreAuthorize("hasAuthority('ROLE:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveRole(@RequestBody RoleDTO roleDTO) {
        try {
            Role role = userService.saveRole(roleDTO);
            return new ResponseEntity<IDResponse>(new IDResponse(role.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", messageSource.getMessage("db.NOT_FOUND", null, getUserLocale())), HttpStatus.BAD_REQUEST);
        }

    }

    @ApiOperation(
            value = "deleteRole",
            notes = "Delete role",
            response = java.lang.Void.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Something exceptional happened", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('ROLE:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteRole(@PathVariable(value = "id") UUID id) {
        try {

            userService.deleteRole(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", messageSource.getMessage("db.FAILED_TO_DELETE", null, getUserLocale())), HttpStatus.BAD_REQUEST);
        }

    }
}
