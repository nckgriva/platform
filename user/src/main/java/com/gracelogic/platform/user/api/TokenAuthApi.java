package com.gracelogic.platform.user.api;


import com.gracelogic.platform.user.Path;
import com.gracelogic.platform.user.PlatformRole;
import com.gracelogic.platform.user.dto.AuthRequestDTO;
import com.gracelogic.platform.user.dto.TokenDTO;
import com.gracelogic.platform.user.dto.UserDTO;
import com.gracelogic.platform.user.service.UserService;
import com.gracelogic.platform.web.dto.ErrorResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = Path.API_AUTH_TOKEN)
@Secured(PlatformRole.ANONYMOUS)
@Api(value = Path.API_AUTH_TOKEN, tags = {"Auth API"},
        authorizations = @Authorization(value = "MybasicAuth"))
public class TokenAuthApi {

    @Autowired
    private UserService userService;

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
    public ResponseEntity login(AuthRequestDTO authRequestDTO) {
        try {
            TokenDTO tokenDTO = userService.login(authRequestDTO);
            return new ResponseEntity<TokenDTO>(tokenDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ErrorResponse>(null);
        }
    }

}
