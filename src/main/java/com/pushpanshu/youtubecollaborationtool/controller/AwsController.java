package com.pushpanshu.youtubecollaborationtool.controller;

import com.pushpanshu.youtubecollaborationtool.model.FileType;
import com.pushpanshu.youtubecollaborationtool.services.AwsService;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/api/s3")
public class AwsController {

    @Value("${cloud.aws.s3.bucket}")
    String bucketName;
    @Autowired
    private AwsService service;

    // Endpoint to list files in a bucket
    @GetMapping("/media")
    public ResponseEntity<?> listFiles() {

        val body = service.listFiles(bucketName);
        return ResponseEntity.ok(body);
    }

    // Endpoint to upload a file to a bucket
    @PostMapping("/upload")
    @SneakyThrows(IOException.class)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {


        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        int dotIndex = file.getOriginalFilename().indexOf(".");
        String fileName = StringUtils.cleanPath(
                Objects.requireNonNull(
                        file.getOriginalFilename().substring(0, dotIndex) + LocalDateTime.now()
                ).replaceAll("[:&.]", "_") // Use replaceAll instead of replace
        );

        fileName += StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename().substring(dotIndex)));

        String contentType = file.getContentType();
        long fileSize = file.getSize();
        InputStream inputStream = file.getInputStream();

        service.uploadFile(bucketName, fileName, fileSize, contentType, inputStream);

        return ResponseEntity.ok().body("File uploaded successfully");
    }

    // Endpoint to download a file from a bucket
    @SneakyThrows
    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable("fileName") String fileName) {


        val body = service.downloadFile(bucketName, fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(FileType.fromFilename(fileName))
                .body(body.toByteArray());
    }

    // Endpoint to delete a file from a bucket
    @DeleteMapping("/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable("fileName") String fileName) {


        service.deleteFile(bucketName, fileName);
        return ResponseEntity.ok().build();
    }
}