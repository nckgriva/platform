package com.gracelogic.platform.market.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.market.Path;
import com.gracelogic.platform.market.dto.DiscountDTO;
import com.gracelogic.platform.market.exception.InvalidDiscountException;
import com.gracelogic.platform.market.model.Discount;
import com.gracelogic.platform.market.service.MarketService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.user.exception.ForbiddenException;
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

import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_DISCOUNT)
@Api(value = Path.API_DISCOUNT, tags = {"Discount API"},
        authorizations = @Authorization(value = "MybasicAuth"))
public class DiscountApi extends AbstractAuthorizedController {

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @Autowired
    private MarketService marketService;

    @ApiOperation(
            value = "getDiscount",
            notes = "Get discount",
            response = DiscountDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Object not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('DISCOUNT:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getDiscount(@PathVariable(value = "id") UUID id,
                                   @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich) {
        try {
            DiscountDTO discountDTO = marketService.getDiscount(id, enrich);
            return new ResponseEntity<DiscountDTO>(discountDTO, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "saveDiscount",
            notes = "Save discount",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveDiscount(@RequestBody DiscountDTO discountDTO) {
        try {
            Discount discount = marketService.saveDiscount(discountDTO);
            return new ResponseEntity<IDResponse>(new IDResponse(discount.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } 
    }

    @ApiOperation(
            value = "deleteDiscount",
            notes = "Delete discount",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Failed to delete discount", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('DISCOUNT:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteDiscount(@PathVariable(value = "id") UUID id) {
        try {
            marketService.deleteDiscount(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "getDiscounts",
            notes = "Get list of discounts",
            response =  EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('DISCOUNT:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getDiscounts(
            @RequestParam(value = "usedForOrderId", required = false) UUID usedForOrderId,
            @RequestParam(value = "discountTypeId", required = false) UUID discountTypeId,
            @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
            @RequestParam(value = "withProducts", required = false, defaultValue = "false") Boolean withProducts,
            @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
            @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
            @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
        EntityListResponse<DiscountDTO> docs = marketService.getDiscountsPaged(usedForOrderId, discountTypeId, enrich, withProducts, length, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<DiscountDTO>>(docs, HttpStatus.OK);
    }
}
