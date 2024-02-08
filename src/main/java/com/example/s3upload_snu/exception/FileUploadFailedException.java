package com.example.s3upload_snu.exception;

import org.apache.tomcat.util.http.fileupload.FileUploadException;

public class FileUploadFailedException extends FileUploadException {

    public FileUploadFailedException(String msg) {
        super(msg);
    }
}
