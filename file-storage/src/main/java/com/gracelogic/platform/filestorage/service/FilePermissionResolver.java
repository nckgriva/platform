package com.gracelogic.platform.filestorage.service;

import com.gracelogic.platform.filestorage.model.StoredFile;
import com.gracelogic.platform.user.dto.AuthorizedUser;

public interface FilePermissionResolver {
    boolean canRead(StoredFile storedFile, AuthorizedUser user);
}
