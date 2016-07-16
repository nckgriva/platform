package com.gracelogic.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Author: Igor Parkhomenko
 * Date: 24.04.2015
 * Time: 16:42
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {
}
