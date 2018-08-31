package com.gracelogic.platform.filestorage.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.google.common.io.Files;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.filestorage.dto.StoredFileDTO;
import com.gracelogic.platform.filestorage.exception.StoredFileDataUnavailableException;
import com.gracelogic.platform.filestorage.exception.UnsupportedStoreModeException;
import com.gracelogic.platform.filestorage.model.StoreMode;
import com.gracelogic.platform.filestorage.model.StoredFile;
import com.gracelogic.platform.filestorage.model.StoredFileData;
import com.gracelogic.platform.property.service.PropertyService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private static Logger logger = Logger.getLogger(FileStorageServiceImpl.class);


    private AmazonS3 s3client = null;

    private AmazonS3 getAmazonS3Client() {
        if (s3client == null) {
            AWSCredentials credentials = new BasicAWSCredentials(propertyService.getPropertyValue("file-storage:s3_access_key"), propertyService.getPropertyValue("file-storage:s3_secret_key"));
            s3client = new AmazonS3Client(credentials);
        }
        return s3client;
    }

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
            storedFile.setDataAvailable(
                    (storeModeId.equals(DataConstants.StoreModes.LOCAL.getValue()) && is != null) ||
                            (storeModeId.equals(DataConstants.StoreModes.DATABASE.getValue()) && is != null) ||
                            storeModeId.equals(DataConstants.StoreModes.EXTERNAL_LINK.getValue()) ||
                            storeModeId.equals(DataConstants.StoreModes.S3.getValue())
            );
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
            } else if (storeModeId.equals(DataConstants.StoreModes.S3.getValue())) {
                if (is != null) {
                    saveDataViaS3(is, storedFile.getId(), referenceObjectId, extension);
                }
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

        if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.LOCAL.getValue())) {
            deleteDataLocally(storedFile.getId(), storedFile.getReferenceObjectId(), storedFile.getExtension());
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
        } else if (storedFile.getStoreMode().getId().equals(DataConstants.StoreModes.S3.getValue())) {
            if (is != null) {
                saveDataViaS3(is, storedFile.getId(), storedFile.getReferenceObjectId(), extension);
            }
        }
    }

    private void deleteDataLocally(UUID storedFileId, UUID referenceObjectId, String extension) {
        String storingPath = buildLocalStoringPath(storedFileId, referenceObjectId, extension);
        File file = new java.io.File(storingPath);
        if (file.exists()) {
            file.delete();
        }
    }

    private void saveDataLocally(InputStream is, UUID storedFileId, UUID referenceObjectId, String extension) throws IOException {
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

    private PutObjectResult saveDataViaS3(InputStream is, UUID storedFileId, UUID referenceObjectId, String extension) throws IOException {
        PutObjectResult result = null;
        try {
            result = getAmazonS3Client().putObject(new PutObjectRequest(
                    propertyService.getPropertyValue("file-storage:s3_bucket_prefix") + referenceObjectId,
                    storedFileId.toString(), is, null));
        } catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            logger.error("Error Message:    " + ase.getMessage() + "\n" +
                    "HTTP Status Code: " + ase.getStatusCode() + "\n" +
                    "AWS Error Code:   " + ase.getErrorCode() + "\n" +
                    "Error Type:       " + ase.getErrorType() + "\n" +
                    "Request ID:       " + ase.getRequestId() + "\n");
            throw new IOException(ase);
        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            throw new IOException(ace);
        }
        return result;
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

        EntityListResponse<StoredFileDTO> entityListResponse = new EntityListResponse<StoredFileDTO>(totalCount, count, page, start);

        List<StoredFile> items = idObjectService.getList(StoredFile.class, fetches, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);
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

    @Override
    public StoredFileDTO getStoredFile(UUID id, boolean enrich) throws ObjectNotFoundException {
        StoredFile storedFile = idObjectService.getObjectById(StoredFile.class, enrich ? "left join fetch el.storeMode" : null, id);
        if (storedFile == null) {
            throw new ObjectNotFoundException();
        }

        StoredFileDTO storedFileDTO = StoredFileDTO.prepare(storedFile);
        if (enrich) {
            StoredFileDTO.enrich(storedFileDTO, storedFile);
        }

        return storedFileDTO;
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

    @Override
    public String buildS3Url(UUID id, UUID referenceObjectId, String extension) {
        String bucket = propertyService.getPropertyValue("file-storage:s3_bucket_prefix") + referenceObjectId;
        return String.format("https://%s.s3.amazonaws.com/%s", bucket, id.toString());
    }
}
