package com.gracelogic.platform.filestorage.dto;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * Author: Igor Parkhomenko
 * Date: 27.02.2015
 * Time: 11:22
 */
public class StoreRequestDTO {
    private CommonsMultipartFile content;
    private String meta;

    public CommonsMultipartFile getContent() {
        return content;
    }

    public void setContent(CommonsMultipartFile content) {
        this.content = content;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }
}
