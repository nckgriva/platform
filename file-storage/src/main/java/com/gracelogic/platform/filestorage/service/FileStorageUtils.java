package com.gracelogic.platform.filestorage.service;

public class FileStorageUtils {
    public static String getFileExtension(String fileName) {
        String extension = null;
        if (fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        }

        return extension;
    }
}
