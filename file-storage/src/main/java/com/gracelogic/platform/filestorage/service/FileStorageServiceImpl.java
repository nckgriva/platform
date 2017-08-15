package com.gracelogic.platform.filestorage.service;

import com.google.common.io.Files;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.filestorage.dto.StoredFileDTO;
import com.gracelogic.platform.filestorage.exception.StoredFileDataUnavailableException;
import com.gracelogic.platform.filestorage.exception.UnsupportedStoreModeException;
import com.gracelogic.platform.filestorage.model.StoreMode;
import com.gracelogic.platform.filestorage.model.StoredFile;
import com.gracelogic.platform.filestorage.model.StoredFileData;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    @Autowired
    private DictionaryService ds;

    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private PropertyService propertyService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public StoredFile createStoredFile(UUID storeModeId, UUID referenceObjectId, InputStream is, String extension, String meta) throws UnsupportedStoreModeException, IOException {
        if (storeModeId != null) {
            if (referenceObjectId == null) {
                referenceObjectId = UUID.fromString("00000000-0000-0000-0000-000000000000");
            }

            StoredFile storedFile = new StoredFile();
            storedFile.setStoreMode(ds.get(StoreMode.class, storeModeId));
            storedFile.setExtension(extension);
            storedFile.setReferenceObjectId(referenceObjectId);
            storedFile.setDataAvailable((storeModeId.equals(DataConstants.StoreModes.LOCAL.getValue()) || storeModeId.equals(DataConstants.StoreModes.DATABASE.getValue())) && is != null);
            storedFile.setMeta(meta);
            storedFile = idObjectService.save(storedFile);

            if (storeModeId.equals(DataConstants.StoreModes.LOCAL.getValue())) {
                if (is != null) {
                    saveDataLocally(is, storedFile.getId(), referenceObjectId, extension);
                }
            } else if (storeModeId.equals(DataConstants.StoreModes.EXTERNAL_LINK.getValue())) {
                //Nothing to do
            } else if (storeModeId.equals(DataConstants.StoreModes.DATABASE.getValue())) {
                StoredFileData storedFileData = new StoredFileData();
                storedFileData.setData(is != null ? IOUtils.toByteArray(is) : null);
                storedFileData = idObjectService.save(storedFileData);
                storedFile.setStoredFileData(storedFileData);
            } else {
                throw new UnsupportedStoreModeException("UnsupportedStoreModeException");
            }

            return storedFile;
        }
        throw new UnsupportedStoreModeException("UnsupportedStoreModeException");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStoredFile(UUID id, InputStream is, String extension, String meta) throws ObjectNotFoundException, IOException {
        StoredFile storedFile = idObjectService.getObjectById(StoredFile.class, id);
        if (storedFile == null) {
            throw new ObjectNotFoundException();
        }

        if (storedFile.getDataAvailable()) {
            if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.LOCAL.getValue())) {
                deleteDataLocally(storedFile.getId(), storedFile.getReferenceObjectId(), storedFile.getExtension());
            }
        }

        storedFile.setDataAvailable((storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.LOCAL.getValue()) ||
                storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.DATABASE.getValue())) && is != null);
        storedFile.setMeta(meta);
        storedFile.setExtension(extension);
        idObjectService.save(storedFile);

        if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.LOCAL.getValue())) {
            if (is != null) {
                saveDataLocally(is, storedFile.getId(), storedFile.getReferenceObjectId(), storedFile.getExtension());
            }
        } else if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.EXTERNAL_LINK.getValue())) {
            //Nothing to do
        } else if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.DATABASE.getValue())) {
            if (is != null) {
                if (storedFile.getStoredFileData() == null) {
                    throw new IOException("StoredFileData is null");
                }
                StoredFileData storedFileData = idObjectService.getObjectById(StoredFileData.class, storedFile.getStoredFileData().getId());
                storedFileData.setData(IOUtils.toByteArray(is));
                idObjectService.save(storedFileData);
            }
        }
    }

    protected void deleteDataLocally(UUID storedFileId, UUID referenceObjectId, String extension) {
        String storingPath = buildLocalStoringPath(storedFileId, referenceObjectId, extension);
        File file = new java.io.File(storingPath);
        if (file.exists()) {
            file.delete();
        }
    }

    protected void saveDataLocally(InputStream is, UUID storedFileId, UUID referenceObjectId, String extension) throws IOException {
        //Create dir
        String basePath = propertyService.getPropertyValue("file-storage:local_store_path");
        basePath = String.format("%s/%s", basePath, referenceObjectId.toString());
        java.io.File file = new java.io.File(basePath);
        file.mkdirs();

        //Save file
        String storingPath = buildLocalStoringPath(storedFileId, referenceObjectId, extension);
        file = new java.io.File(storingPath);
        if (file.exists()) {
            file.delete();
            file = new java.io.File(storingPath);
        }

        FileUtils.copyInputStreamToFile(is, file);
    }

    @Override
    public EntityListResponse<StoredFileDTO> getStoredFilesPaged(UUID referenceObjectId, Boolean dataAvailable, Collection<UUID> storeModeIds, boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = "";
        if (enrich) {
            fetches += "left join fetch el.storeMode ";
        }

        String cause = "1=1 ";

        HashMap<String, Object> params = new HashMap<String, Object>();
        if (referenceObjectId != null) {
            cause += "and el.referenceObjectId = :referenceObjectId ";
            params.put("referenceObjectId", referenceObjectId);
        }
        if (dataAvailable != null) {
            cause += "and el.dataAvailable = :dataAvailable ";
            params.put("dataAvailable", dataAvailable);
        }
        if (storeModeIds != null && !storeModeIds.isEmpty()) {
            cause += "and el.storeMode.id in (:storeModeIds) ";
            params.put("storeModeIds", storeModeIds);
        }

        int totalCount = idObjectService.getCount(StoredFile.class, null, null, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<StoredFileDTO> entityListResponse = new EntityListResponse<StoredFileDTO>();
        entityListResponse.setEntity("storedFile");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<StoredFile> items = idObjectService.getList(StoredFile.class, fetches, cause, params, sortField, sortDir, startRecord, count);
        entityListResponse.setPartCount(items.size());
        for (StoredFile e : items) {
            StoredFileDTO el = StoredFileDTO.prepare(e);

            if (enrich) {
                StoredFileDTO.enrich(el, e);
            }

            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Override
    public byte[] getStoredFileData(StoredFile storedFile) throws UnsupportedStoreModeException, StoredFileDataUnavailableException, IOException {
        if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.LOCAL.getValue())) {
            return FileUtils.readFileToByteArray(getFile(storedFile));
        } else if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.DATABASE.getValue())) {
            StoredFileData storedFileData = idObjectService.getObjectById(StoredFileData.class, storedFile.getStoredFileData().getId());
            return storedFileData.getData();
        } else {
            throw new UnsupportedStoreModeException("UnsupportedStoreModeException");
        }

    }

    @Override
    public void writeStoredFileDataToOutputStream(StoredFile storedFile, OutputStream os) throws UnsupportedStoreModeException, StoredFileDataUnavailableException, IOException {
        if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.LOCAL.getValue())) {
            Files.copy(getFile(storedFile), os);
        } else if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.DATABASE.getValue())) {
            StoredFileData storedFileData = idObjectService.getObjectById(StoredFileData.class, storedFile.getStoredFileData().getId());
            os.write(storedFileData.getData());
        } else {
            throw new UnsupportedStoreModeException("UnsupportedStoreModeException");
        }

    }

    @Override
    public File getFile(StoredFile storedFile) throws UnsupportedStoreModeException, StoredFileDataUnavailableException {
        if (!storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.LOCAL.getValue())) {
            throw new UnsupportedStoreModeException("UnsupportedStoreModeException");
        }
        if (!storedFile.getDataAvailable()) {
            throw new StoredFileDataUnavailableException("StoredFileDataUnavailableException");
        }

        return new File(buildLocalStoringPath(storedFile.getId(), storedFile.getReferenceObjectId(), storedFile.getExtension()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteStoredFile(UUID id, boolean withContent) {
        StoredFile storedFile = idObjectService.getObjectById(StoredFile.class, id);
        idObjectService.delete(StoredFile.class, id);
        if (withContent) {
            if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.LOCAL.getValue())) {
                deleteDataLocally(storedFile.getId(), storedFile.getReferenceObjectId(), storedFile.getExtension());
            } else if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.DATABASE.getValue())) {
                try {
                    idObjectService.delete(StoredFileData.class, storedFile.getStoredFileData().getId());
                } catch (Exception ignored) {
                } //If many StoredFile link to this StoredFileData
            }
        }
    }

    @Override
    public String buildLocalStoringPath(UUID id, UUID referenceObjectId, String extension) {
        String basePath = propertyService.getPropertyValue("file-storage:local_store_path");
        basePath = String.format("%s/%s", basePath, referenceObjectId.toString());
        return String.format("%s/%s.%s", basePath, id.toString(), extension);
    }
}
