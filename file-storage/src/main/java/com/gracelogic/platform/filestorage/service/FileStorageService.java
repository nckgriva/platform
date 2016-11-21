package com.gracelogic.platform.filestorage.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.filestorage.dto.StoredFileDTO;
import com.gracelogic.platform.filestorage.exception.StoredFileDataUnavailableException;
import com.gracelogic.platform.filestorage.exception.UnsupportedStoreModeException;
import com.gracelogic.platform.filestorage.model.StoredFile;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

public interface FileStorageService {
    StoredFile storeFile(UUID storeModeId, UUID referenceObjectId, byte[] data, String extension) throws UnsupportedStoreModeException, IOException;

    EntityListResponse<StoredFileDTO> getStoredFilesPaged(UUID referenceObjectId, Boolean dataAvailable, Collection<UUID> storeModeIds, Integer count, Integer page, Integer start, String sortField, String sortDir);

    byte[] getStoredFileData(StoredFile storedFile) throws UnsupportedStoreModeException, StoredFileDataUnavailableException, IOException;
}
