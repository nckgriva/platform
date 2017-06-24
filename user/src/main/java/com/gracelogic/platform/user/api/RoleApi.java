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
public class RoleApi extends AbstractAuthorizedController {
    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private UserService userService;

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
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getRole(@PathVariable(value = "id") UUID id,
                                  @RequestParam(value = "fetchGrants", required = false, defaultValue = "false") Boolean fetchGrants) {
        try {
            RoleDTO roleDTO = userService.getRole(id, fetchGrants);
            return new ResponseEntity<>(roleDTO, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", messageSource.getMessage("db.NOT_FOUND", null, getUserLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('ROLE:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveRole(@RequestBody RoleDTO roleDTO) {
        try {
            Role role = userService.saveRole(roleDTO);
            return new ResponseEntity<>(new IDResponse(role.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", messageSource.getMessage("db.NOT_FOUND", null, getUserLocale())), HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasAuthority('ROLE:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteRole(@PathVariable(value = "id") UUID id) {
        try {

            userService.deleteRole(id);
            return new ResponseEntity<>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", messageSource.getMessage("db.FAILED_TO_DELETE", null, getUserLocale())), HttpStatus.BAD_REQUEST);
        }

    }
}
