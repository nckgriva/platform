package com.gracelogic.platform.filestorage.dto;

import org.springframework.web.multipart.MultipartFile;

public class StoreRequestDTO {
    private MultipartFile content;
    private String meta;

    public MultipartFile getContent() {
        return content;
    }

    public void setContent(MultipartFile content) {
        this.content = content;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }
}
