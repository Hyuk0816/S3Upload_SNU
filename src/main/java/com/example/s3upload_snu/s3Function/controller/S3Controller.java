package com.example.s3upload_snu.s3Function.controller;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.example.s3upload_snu.exception.FileUploadFailedException;
import com.example.s3upload_snu.s3Function.service.S3FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Cacheable;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
@RequiredArgsConstructor
@Slf4j
@Cacheable
@RestController
@Tag(name = "S3 업로드 및 다운로드")
@RequestMapping("/api/file")
public class S3Controller {

    private final S3FileService fileService;
    @Operation(summary = "파일 업로드")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws FileUploadFailedException {
        return fileService.uploadFile(file);
    }

    @Operation(summary = "파일 다운로드")
    @GetMapping("/download/{folder}/{filename}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String folder, @PathVariable String filename) throws IOException {
        return fileService.downloadFile(folder, filename);
    }

    @Operation(summary = "버킷 파일 조회")
    @GetMapping("/search/{folder}")
    @ResponseBody
    public List<S3ObjectSummary> objectsInBucket(@PathVariable String folder){
        return fileService.objectsInBucket(folder);
    }
}

