package com.gracelogic.platform.user.api;


import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.user.PlatformRole;
import com.gracelogic.platform.user.dto.AuthRequestDTO;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.IdentifierDTO;
import com.gracelogic.platform.user.dto.TokenDTO;
import com.gracelogic.platform.user.model.Token;
import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.user.service.UserLifecycleService;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.ServletUtils;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = Path.API_AUTH_TOKEN)
@Secured(PlatformRole.ANONYMOUS)
@Api(value = Path.API_AUTH_TOKEN, tags = {"Token Auth API"},
        authorizations = @Authorization(value = "MybasicAuth"))
public class TokenAuthApi {

    @Autowired
    private UserService userService;

    @Autowired
    private UserLifecycleService lifecycleService;

    @ApiOperation(
            value = "signIn",
            notes = "Sign in",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Invalid credentials"),
            @ApiResponse(code = 423, message = "User blocked"),
            @ApiResponse(code = 429, message = "Too many attempts"),
            @ApiResponse(code = 422, message = "Not activated"),
            @ApiResponse(code = 510, message = "Not allowed IP"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/sign-in", method = RequestMethod.POST)
    public ResponseEntity signIn(HttpServletRequest request, HttpServletResponse response, @RequestBody AuthRequestDTO authRequestDTO) {
        try {
            Token token = userService.establishToken(authRequestDTO, ServletUtils.getRemoteAddress(request));

            User user = token.getIdentifier().getUser();
            AuthorizedUser authorizedUser = AuthorizedUser.prepare(user);
            authorizedUser.setSignInIdentifier(IdentifierDTO.prepare(token.getIdentifier()));
            lifecycleService.signIn(authorizedUser);

            return new ResponseEntity<TokenDTO>(new TokenDTO(token.getId()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ErrorResponse>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "signOut",
            notes = "Sign out user",
            response = ResponseEntity.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/sign-out", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity signOut(@RequestBody TokenDTO tokenDTO) {
        try {
            userService.deactivateToken(tokenDTO);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception ignored) {
            return new ResponseEntity<ErrorResponse>(HttpStatus.BAD_REQUEST);
        }
    }

}
