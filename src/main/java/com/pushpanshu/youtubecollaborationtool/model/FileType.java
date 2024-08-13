package com.pushpanshu.youtubecollaborationtool.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.springframework.http.MediaType;

import java.util.Arrays;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PACKAGE)
public enum FileType {
    JPG("jpg", MediaType.IMAGE_JPEG),
    JPEG("jpeg", MediaType.IMAGE_JPEG),
    PNG("png", MediaType.IMAGE_PNG),
    MP4(".mp4", MediaType.APPLICATION_OCTET_STREAM),
    SRT("srt", MediaType.APPLICATION_OCTET_STREAM);

    private final String extension;

    private final MediaType mediaType;

    public static MediaType fromFilename(String fileName) {
        // Finding the last index of '.' to get the extension
        val dotIndex = fileName.lastIndexOf('.');
        // Extracting file extension from filename
        val fileExtension = (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
        // Finding matching enum constant for the file extension
        return Arrays.stream(values())
                .filter(e -> e.getExtension().equals(fileExtension))
                .findFirst()
                .map(FileType::getMediaType)
                .orElse(MediaType.APPLICATION_OCTET_STREAM); // Default to octet-stream if no matching media type found
    }
}