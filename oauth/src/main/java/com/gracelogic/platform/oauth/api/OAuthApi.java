package com.gracelogic.platform.oauth.api;

import com.gracelogic.platform.oauth.Path;
import com.gracelogic.platform.oauth.dto.AuthProviderDTO;
import com.gracelogic.platform.oauth.service.OAuthService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequestMapping(value = Path.API_OAUTH)
public class OAuthApi extends AbstractAuthorizedController {

    @Autowired
    private OAuthService oAuthService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity providers() {

        List<AuthProviderDTO> providers = oAuthService.getAuthProviders();
        return new ResponseEntity<List<AuthProviderDTO>>(providers, HttpStatus.OK);
    }
}
