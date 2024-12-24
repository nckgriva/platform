package com.gracelogic.platform.market.api;

import com.gracelogic.platform.account.exception.CurrencyMismatchException;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.market.Path;
import com.gracelogic.platform.market.dto.DiscountDTO;
import com.gracelogic.platform.market.model.Discount;
import com.gracelogic.platform.market.service.MarketService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
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
@RequestMapping(value = Path.API_DISCOUNT)
public class DiscountApi extends AbstractAuthorizedController {

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @Autowired
    @Qualifier("accountMessageSource")
    private ResourceBundleMessageSource accountMessageSource;

    @Autowired
    private MarketService marketService;

    @PreAuthorize("hasAuthority('DISCOUNT:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getDiscount(@PathVariable(value = "id") UUID id,
                                      @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                      @RequestParam(value = "withProducts", required = false, defaultValue = "false") Boolean withProducts) {
        try {
            DiscountDTO discountDTO = marketService.getDiscount(id, enrich, withProducts);
            return new ResponseEntity<DiscountDTO>(discountDTO, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('DISCOUNT:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveDiscount(@RequestBody DiscountDTO discountDTO) {
        try {
            Discount discount = marketService.saveDiscount(discountDTO);
            return new ResponseEntity<IDResponse>(new IDResponse(discount.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (CurrencyMismatchException e) {
            return new ResponseEntity<>(new ErrorResponse("account.CURRENCY_MISMATCH", accountMessageSource.getMessage("account.CURRENCY_MISMATCH", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

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

    @PreAuthorize("hasAuthority('DISCOUNT:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getDiscounts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "usedForOrderId", required = false) UUID usedForOrderId,
            @RequestParam(value = "discountTypeId", required = false) UUID discountTypeId,
            @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
            @RequestParam(value = "calculate", required = false, defaultValue = "false") Boolean calculate,
            @RequestParam(value = "withProducts", required = false, defaultValue = "false") Boolean withProducts,
            @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
            @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
            @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
        EntityListResponse<DiscountDTO> docs = marketService.getDiscountsPaged(name, usedForOrderId, discountTypeId, enrich, calculate, withProducts, length, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<DiscountDTO>>(docs, HttpStatus.OK);
    }
}
