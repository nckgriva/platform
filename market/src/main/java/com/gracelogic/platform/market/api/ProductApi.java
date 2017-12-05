package com.gracelogic.platform.market.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.market.Path;
import com.gracelogic.platform.market.dto.ProductDTO;
import com.gracelogic.platform.market.exception.PrimaryProductException;
import com.gracelogic.platform.market.model.Product;
import com.gracelogic.platform.market.service.MarketService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
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
@RequestMapping(value = Path.API_PRODUCT)
@Api(value = Path.API_PRODUCT, tags = {"Product API"},
        authorizations = @Authorization(value = "MybasicAuth"))
public class ProductApi extends AbstractAuthorizedController {

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @Autowired
    @Qualifier("marketMessageSource")
    private ResourceBundleMessageSource marketMessageSource;

    @Autowired
    private MarketService marketService;

    @ApiOperation(
            value = "getProduct",
            notes = "Get product",
            response = ProductDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Object not found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('PRODUCT:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getProduct(@PathVariable(value = "id") UUID id,
                                      @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich) {
        try {
            ProductDTO productDTO = marketService.getProduct(id, enrich);
            return new ResponseEntity<ProductDTO>(productDTO, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "saveProduct",
            notes = "Save product",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('PRODUCT:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveProduct(@RequestBody ProductDTO productDTO) {
        try {
            Product product = marketService.saveProduct(productDTO);
            return new ResponseEntity<IDResponse>(new IDResponse(product.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (PrimaryProductException e) {
            return new ResponseEntity<>(new ErrorResponse("market.PRIMARY_PRODUCT_CONFLICT", marketMessageSource.getMessage("market.PRIMARY_PRODUCT_CONFLICT", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "deleteProduct",
            notes = "Delete product",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Failed to delete product", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('PRODUCT:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteProduct(@PathVariable(value = "id") UUID id) {
        try {
            marketService.deleteProduct(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "getProducts",
            notes = "Get list of products",
            response =  EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('PRODUCT:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "productTypeId", required = false) UUID productTypeId,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
            @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
            @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
            @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
        EntityListResponse<ProductDTO> docs = marketService.getProductsPaged(name, productTypeId, active, enrich, length, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<ProductDTO>>(docs, HttpStatus.OK);
    }
}
