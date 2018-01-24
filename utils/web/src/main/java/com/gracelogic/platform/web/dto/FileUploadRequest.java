package com.gracelogic.platform.web.dto;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class FileUploadRequest {
    private CommonsMultipartFile content;

    public CommonsMultipartFile getContent() {
        return content;
    }

    public void setContent(CommonsMultipartFile content) {
        this.content = content;
    }
}
