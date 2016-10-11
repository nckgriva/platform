package com.gracelogic.platform.web.dto;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Author: Igor Parkhomenko
 * Date: 27.02.2015
 * Time: 11:22
 */
public class FileUploadRequest {
    private CommonsMultipartFile content;

    public CommonsMultipartFile getContent() {
        return content;
    }

    public void setContent(CommonsMultipartFile content) {
        this.content = content;
    }
}
