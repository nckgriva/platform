package com.gracelogic.platform.template.service;

import com.gracelogic.platform.template.dto.LoadedTemplate;

import java.io.IOException;
import java.util.Map;

/**
 * Author: Igor Parkhomenko
 * Date: 17.07.2016
 * Time: 16:41
 */
public interface TemplateService {
    LoadedTemplate load(String templateName) throws IOException;

    String apply(LoadedTemplate template, Map<String, String> params);
}
