package com.gracelogic.platform.filestorage.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.filestorage.Path;
import com.gracelogic.platform.filestorage.dto.StoreRequestDTO;
import com.gracelogic.platform.filestorage.dto.StoredFileDTO;
import com.gracelogic.platform.filestorage.exception.StoredFileDataUnavailableException;
import com.gracelogic.platform.filestorage.exception.UnsupportedStoreModeException;
import com.gracelogic.platform.filestorage.model.StoredFile;
import com.gracelogic.platform.filestorage.service.*;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import io.swagger.annotations.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_FILE_STORAGE)
@Api(value = Path.API_FILE_STORAGE, description = "Контроллер по работе с сохраненными файлами", authorizations = @Authorization(value = "MybasicAuth"))
public class FileStorageApi extends AbstractAuthorizedController {
    @Autowired
    private DownloadServiceImpl downloadService;

    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private FilePermissionResolver filePermissionResolver;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    @Qualifier("filestorageMessageSource")
    private ResourceBundleMessageSource messageSource;

    private static Logger logger = Logger.getLogger(FileStorageApi.class);

    @ApiOperation(value = "updateStoredFile", notes = "Обновить содержимое файла")
    @ApiResponses({@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Something exceptional happened")})
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/update")
    @ResponseBody
    public ResponseEntity updateStoredFile(@PathVariable(value = "id") UUID id,
                                           @ModelAttribute StoreRequestDTO dto) {
        StoredFile storedFile = idObjectService.getObjectById(StoredFile.class, id);

        if (storedFile == null) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("fileStorage.NOT_FOUND", messageSource.getMessage("fileStorage.NOT_FOUND", null, getUserLocale())), HttpStatus.NOT_FOUND);
        }

        if (!filePermissionResolver.canWrite(storedFile, getUser())) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("fileStorage.FORBIDDEN", messageSource.getMessage("fileStorage.FORBIDDEN", null, getUserLocale())), HttpStatus.FORBIDDEN);
        }

        try {
            fileStorageService.updateStoredFile(id, dto.getContent() != null ? dto.getContent().getInputStream() : null, dto.getContent() != null ? FileStorageUtils.getFileExtension(dto.getContent().getOriginalFilename()) : "", dto.getMeta());
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("fileStorage.NOT_FOUND", messageSource.getMessage("fileStorage.NOT_FOUND", null, getUserLocale())), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            logger.error("Failed to update file", e);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("fileStorage.IO_EXCEPTION", messageSource.getMessage("fileStorage.IO_EXCEPTION", null, getUserLocale())), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}/upload", consumes = "application/octet-stream")
    @ResponseBody
    public ResponseEntity uploadStoredFileData(@PathVariable(value = "id") UUID id,
                                               @RequestParam(value = "filename", required = false) String filename,
                                               @RequestParam(value = "meta", required = false) String meta,
                                               HttpServletRequest request) {
        StoredFile storedFile = idObjectService.getObjectById(StoredFile.class, id);
        if (storedFile == null) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("fileStorage.NOT_FOUND", messageSource.getMessage("fileStorage.NOT_FOUND", null, getUserLocale())), HttpStatus.NOT_FOUND);
        }
        if (!filePermissionResolver.canWrite(storedFile, getUser())) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("fileStorage.FORBIDDEN", messageSource.getMessage("fileStorage.FORBIDDEN", null, getUserLocale())), HttpStatus.FORBIDDEN);
        }
        try {
            fileStorageService.updateStoredFile(id, request.getInputStream(), filename, meta);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("fileStorage.NOT_FOUND", messageSource.getMessage("fileStorage.NOT_FOUND", null, getUserLocale())), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Failed to upload file", e);
            return new ResponseEntity<ErrorResponse>(new ErrorResponse("fileStorage.IO_EXCEPTION", messageSource.getMessage("fileStorage.IO_EXCEPTION", null, getUserLocale())), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
    }

    @ApiOperation(value = "downloadStoredFile", notes = "Скачать содержимое файла")
    @ApiResponses({@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Something exceptional happened")})
    @RequestMapping(value = "/{id}/download", method = RequestMethod.GET)
    public void downloadStoredFile(@PathVariable(value = "id") UUID id,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        StoredFile storedFile = idObjectService.getObjectById(StoredFile.class, id);

        if (storedFile == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        if (!filePermissionResolver.canRead(storedFile, getUser())) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.LOCAL.getValue())) {
            File file = new File(fileStorageService.buildLocalStoringPath(storedFile.getId(), storedFile.getReferenceObjectId(), storedFile.getExtension()));

            try {
                downloadService.processRequest(file, request, response, true);
            } catch (IOException e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } else if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.EXTERNAL_LINK.getValue())) {
            try {
                response.sendRedirect(storedFile.getMeta());
            } catch (IOException e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } else if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.DATABASE.getValue())) {
            try {
                fileStorageService.writeStoredFileDataToOutputStream(storedFile, response.getOutputStream());
            } catch (IOException | UnsupportedStoreModeException | StoredFileDataUnavailableException e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
    }

    @ApiOperation(value = "getStoredFiles", notes = "Получить список элементов")
    @ApiResponses({@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Something exceptional happened")})
    @PreAuthorize("hasAuthority('FILE_STORAGE:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getStoredFiles(@ApiParam(name = "referenceObjectId", value = "referenceObjectId") @RequestParam(value = "referenceObjectId", required = true) UUID referenceObjectId,
                                         @ApiParam(name = "storeModeId", value = "storeModeId") @RequestParam(value = "storeModeId", required = false) UUID storeModeId,
                                         @ApiParam(name = "dataAvailable", value = "dataAvailable") @RequestParam(value = "dataAvailable", required = false) Boolean dataAvailable,
                                         @ApiParam(name = "enrich", value = "enrich") @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                         @ApiParam(name = "start", value = "start") @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                         @ApiParam(name = "page", value = "page") @RequestParam(value = "page", required = false) Integer page,
                                         @ApiParam(name = "count", value = "count") @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
                                         @ApiParam(name = "sortField", value = "sortField") @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                         @ApiParam(name = "sortDir", value = "sortDir") @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        EntityListResponse<StoredFileDTO> storedFiles = fileStorageService.getStoredFilesPaged(referenceObjectId, dataAvailable, storeModeId != null ? Collections.singletonList(storeModeId) : null, enrich, length, page, start, sortField, sortDir);

        return new ResponseEntity<EntityListResponse<StoredFileDTO>>(storedFiles, HttpStatus.OK);
    }
}
