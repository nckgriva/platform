package com.gracelogic.platform.filestorage.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.filestorage.Path;
import com.gracelogic.platform.filestorage.dto.StoredFileDTO;
import com.gracelogic.platform.filestorage.model.StoredFile;
import com.gracelogic.platform.filestorage.service.DownloadServiceImpl;
import com.gracelogic.platform.filestorage.service.FilePermissionResolver;
import com.gracelogic.platform.filestorage.service.FileStorageService;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 16.07.2016
 * Time: 21:58
 */
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
    private PropertyService propertyService;

    @Autowired
    private FileStorageService fileStorageService;

    @ApiOperation(value = "downloadFile", notes = "Загрузить содержимое файла")
    @ApiResponses({@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Something exceptional happened")})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable(value = "id") String id,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        StoredFile storedFile = idObjectService.getObjectById(StoredFile.class, UUID.fromString(id));

        if (!filePermissionResolver.canRead(storedFile, getUser())) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        if (storedFile == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }


        String basePath = propertyService.getPropertyValue("file-storage:local_store_path");
        basePath = String.format("%s/%s", basePath, storedFile.getReferenceObjectId().toString());

        File file = new File(String.format("%s/%s.%s", basePath, storedFile.getId().toString(), storedFile.getExtension()));

        try {
            downloadService.processRequest(file, request, response, true);
        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @ApiOperation(value = "elements", notes = "Получить список элементов")
    @ApiResponses({@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Something exceptional happened")})
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getStoredFiles(@ApiParam(name = "referenceObjectId", value = "referenceObjectId") @RequestParam(value = "referenceObjectId", required = true) UUID referenceObjectId,
                                         @ApiParam(name = "storeModeId", value = "storeModeId") @RequestParam(value = "storeModeId", required = false) UUID storeModeId,
                                         @ApiParam(name = "dataAvailable", value = "dataAvailable") @RequestParam(value = "dataAvailable", required = false) Boolean dataAvailable,
                                         @ApiParam(name = "start", value = "start") @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                         @ApiParam(name = "page", value = "page") @RequestParam(value = "page", required = false) Integer page,
                                         @ApiParam(name = "count", value = "count") @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
                                         @ApiParam(name = "sortField", value = "sortField") @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                         @ApiParam(name = "sortDir", value = "sortDir") @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        EntityListResponse<StoredFileDTO> storedFiles = fileStorageService.getStoredFilesPaged(referenceObjectId, dataAvailable, storeModeId != null ? Collections.singletonList(storeModeId) : null, length, page, start, sortField, sortDir);

        return new ResponseEntity<EntityListResponse<StoredFileDTO>>(storedFiles, HttpStatus.OK);
    }
}
