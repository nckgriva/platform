package com.gracelogic.platform.user.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.property.dto.PropertyDTO;
import com.gracelogic.platform.property.model.Property;
import com.gracelogic.platform.property.service.PropertyService;
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

import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_PROPERTY)
public class PropertyApi extends AbstractAuthorizedController {
    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private PropertyService propertyService;

    @PreAuthorize("hasAuthority('PROPERTY:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getProperties(@RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "visible", required = false, defaultValue = "true") Boolean visible,
                                        @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                        @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                        @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                        @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                        @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        EntityListResponse<PropertyDTO> properties = propertyService.getPropertiesPaged(name, visible, enrich, count, null, start, sortField, sortDir);
        return new ResponseEntity<>(properties, HttpStatus.OK);

    }

    @PreAuthorize("hasAuthority('PROPERTY:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getProperty(@PathVariable(value = "id") UUID id) {
        try {
            PropertyDTO propertyDTO = propertyService.getProperty(id);
            return new ResponseEntity<>(propertyDTO, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", messageSource.getMessage("db.NOT_FOUND", null, getUserLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('PROPERTY:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveProperty(@RequestBody PropertyDTO propertyDTO) {
        try {
            Property property = propertyService.saveProperty(propertyDTO);
            return new ResponseEntity<>(new IDResponse(property.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", messageSource.getMessage("db.NOT_FOUND", null, getUserLocale())), HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasAuthority('PROPERTY:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteProperty(@PathVariable(value = "id") UUID id) {
        try {
            propertyService.deleteProperty(id);
            return new ResponseEntity<>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", messageSource.getMessage("db.FAILED_TO_DELETE", null, getUserLocale())), HttpStatus.BAD_REQUEST);
        }

    }

}
