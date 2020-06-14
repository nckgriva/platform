package com.gracelogic.platform.user.service.token;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.user.dto.TokenDTO;
import com.gracelogic.platform.user.exception.UserNotFoundException;
import com.gracelogic.platform.user.model.Token;

import java.util.UUID;

public interface TokenService {

    EntityListResponse<TokenDTO> getTokensPaged(UUID identifierId, UUID userId, boolean enrich, boolean calculate, Integer count, Integer page, Integer start, String sortField, String sortDir);

    TokenDTO getToken(UUID id) throws ObjectNotFoundException;

    Token saveToken(TokenDTO dto) throws ObjectNotFoundException, UserNotFoundException;

    void deleteToken(UUID id);



}
