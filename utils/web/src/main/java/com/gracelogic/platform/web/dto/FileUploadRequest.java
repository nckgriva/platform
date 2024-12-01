package com.gracelogic.platform.web.dto;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadRequest {
    private MultipartFile content;

    public MultipartFile getContent() {
        return content;
    }

    public void setContent(MultipartFile content) {
        this.content = content;
    }
}
