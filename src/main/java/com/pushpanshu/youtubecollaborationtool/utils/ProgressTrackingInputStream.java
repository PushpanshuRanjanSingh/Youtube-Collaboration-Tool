package com.pushpanshu.youtubecollaborationtool.utils;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.InputStream;

public class ProgressTrackingInputStream extends InputStream {
    private final InputStream wrappedInputStream;
    private final long totalSize;
    private final SseEmitter emitter;
    private long bytesRead = 0;

    public ProgressTrackingInputStream(InputStream inputStream, long totalSize, SseEmitter emitter) {
        this.wrappedInputStream = inputStream;
        this.totalSize = totalSize;
        this.emitter = emitter;
    }

    @Override
    public int read() throws IOException {
        int data = wrappedInputStream.read();
        if (data != -1) {
            bytesRead++;
            sendProgress();
        }
        return data;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesReadNow = wrappedInputStream.read(b, off, len);
        if (bytesReadNow != -1) {
            bytesRead += bytesReadNow;
            sendProgress();
        }
        return bytesReadNow;
    }

    private void sendProgress() throws IOException {
        double progress = (double) bytesRead / totalSize * 100;
        emitter.send(String.format("%.2f", progress) + "%", MediaType.APPLICATION_JSON);
    }

    @Override
    public void close() throws IOException {
        wrappedInputStream.close();
    }
}