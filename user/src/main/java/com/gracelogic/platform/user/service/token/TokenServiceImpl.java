package com.gracelogic.platform.user.service.token;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.user.dto.TokenDTO;
import com.gracelogic.platform.user.exception.InvalidIdentifierException;
import com.gracelogic.platform.user.exception.UserNotFoundException;
import com.gracelogic.platform.user.model.Identifier;
import com.gracelogic.platform.user.model.Token;
import com.gracelogic.platform.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private IdObjectService idObjectService;

    @Override
    public EntityListResponse<TokenDTO> getTokensPaged(UUID identifierId, UUID userId, boolean enrich, boolean calculate, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = "";
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (userId != null) {
            cause += " and el.user.id = :userId";
            params.put("userId", userId);
        }

        if (identifierId != null) {
            cause += " and el.identifier.id = :identifierId";
            params.put("identifierId", identifierId);
        }

        Integer totalCount = calculate ? idObjectService.getCount(Token.class, null, countFetches, cause, params) : null;

        EntityListResponse<TokenDTO> entityListResponse = new EntityListResponse<TokenDTO>(totalCount, count, page, start);

        List<Token> items = idObjectService.getList(Token.class, fetches, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);
        for (Token e : items) {
            TokenDTO el = TokenDTO.prepare(e, enrich);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Override
    public TokenDTO getToken(UUID id) throws ObjectNotFoundException {
        Token entity = idObjectService.getObjectById(Token.class, " left join fetch el.user left join fetch el.identifier ", id);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }

        TokenDTO dto = TokenDTO.prepare(entity, true);

        return dto;
    }

    @Override
    @Transactional
    public Token saveToken(TokenDTO dto) throws ObjectNotFoundException, UserNotFoundException, InvalidIdentifierException {

        Token entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(Token.class, "left join fetch el.user left join fetch el.identifier ", dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new Token();
        }


        User user = idObjectService.getObjectById(User.class, dto.getUserId());
        if (user == null) {
            throw new UserNotFoundException();
        }

        Identifier identifier = idObjectService.getObjectById(Identifier.class, dto.getIdentifierId());
        if (identifier == null) {
            throw new InvalidIdentifierException();
        }

        if (!identifier.getUser().getId().equals(user.getId())) {
            throw new UserNotFoundException();
        }

        entity.setUser(user);
        entity.setIdentifier(identifier);
        entity.setActive(dto.isActive());
        entity.setLastRequest(dto.getLastRequest());

        return idObjectService.save(entity);
    }

    @Override
    @Transactional
    public void deleteToken(UUID id) { idObjectService.delete(Token.class, id); }
}
