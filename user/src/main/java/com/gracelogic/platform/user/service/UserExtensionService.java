package com.gracelogic.platform.user.service;

import com.gracelogic.platform.user.dto.AuthorizedUser;

public interface UserExtensionService {
    AuthorizedUser extendUser(AuthorizedUser user);
}
