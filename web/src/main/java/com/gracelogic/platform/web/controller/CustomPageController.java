package com.gracelogic.platform.web.controller;

import com.gracelogic.platform.web.Path;
import com.gracelogic.platform.web.exception.BadRequestException;
import com.gracelogic.platform.web.exception.InternalServerErrorException;
import com.gracelogic.platform.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: Igor Parkhomenko
 * Date: 24.04.2015
 * Time: 15:42
 */
@Controller
@RequestMapping(value = Path.PLATFORM_CUSTOM)
public class CustomPageController {

    @RequestMapping(method = RequestMethod.GET, value = "/{pageName}")
    public ModelAndView getPage(@PathVariable(value = "pageName") String pageName,
                                HttpServletRequest request)
    {
        return new ModelAndView(String.format("custom/%s", pageName));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/404")
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView getNotFoundPage(HttpServletRequest request) {
        return new ModelAndView("custom/404");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/400")
    @ExceptionHandler(BadRequestException.class)
    public ModelAndView getBadRequestPage(HttpServletRequest request) {
        return new ModelAndView("custom/400");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/500")
    @ExceptionHandler(InternalServerErrorException.class)
    public ModelAndView getInternalServerErrorPage(HttpServletRequest request) {
        return new ModelAndView("custom/500");
    }
}
