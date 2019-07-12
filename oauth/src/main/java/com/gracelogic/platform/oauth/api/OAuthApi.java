package com.gracelogic.platform.oauth.api;

import com.gracelogic.platform.oauth.Path;
import com.gracelogic.platform.oauth.dto.AuthProviderDTO;
import com.gracelogic.platform.oauth.service.OAuthService;
import com.gracelogic.platform.user.PlatformRole;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@Secured(PlatformRole.ANONYMOUS)
@RequestMapping(value = Path.API_OAUTH)
@Api(value = Path.API_OAUTH, tags = {"OAuth API"})
public class OAuthApi extends AbstractAuthorizedController {
    @Autowired
    private OAuthService oAuthService;

    @ApiOperation(
            value = "providers",
            notes = "Return all available oauth providers",
            response = ResponseEntity.class
    )
    @RequestMapping(value = "providers", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity providers() {
        List<AuthProviderDTO> providers = oAuthService.getAuthProviders();
        return new ResponseEntity<List<AuthProviderDTO>>(providers, HttpStatus.OK);
    }
}
