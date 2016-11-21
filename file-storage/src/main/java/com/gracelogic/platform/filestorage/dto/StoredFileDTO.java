package com.gracelogic.platform.filestorage.dto;

import com.gracelogic.platform.db.dto.IdObjectModel;
import com.gracelogic.platform.filestorage.model.StoredFile;

import java.util.UUID;

public class StoredFileDTO extends IdObjectModel {
    private String extension;
    private UUID referenceObjectId;
    private UUID storeModeId;

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public UUID getReferenceObjectId() {
        return referenceObjectId;
    }

    public void setReferenceObjectId(UUID referenceObjectId) {
        this.referenceObjectId = referenceObjectId;
    }

    public UUID getStoreModeId() {
        return storeModeId;
    }

    public void setStoreModeId(UUID storeModeId) {
        this.storeModeId = storeModeId;
    }

    public static StoredFileDTO prepare(StoredFile storedFile) {
        StoredFileDTO dto = new StoredFileDTO();
        IdObjectModel.prepare(dto, storedFile);

        dto.setReferenceObjectId(storedFile.getReferenceObjectId());
        dto.setExtension(storedFile.getExtension());
        if (storedFile.getStoreMode() != null) {
            dto.setStoreModeId(storedFile.getStoreMode().getId());
        }

        return dto;
    }
}
