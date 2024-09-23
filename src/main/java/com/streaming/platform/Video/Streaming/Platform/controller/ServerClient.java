package com.streaming.platform.Video.Streaming.Platform.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

    @RestController
    public class ServerClient {
        private static final String VIDEO_PATH = "D:\\MY_VIDEO.mp4";
        private static final String AUDIO_PATH = "D:\\Aayi Nai.mp3";
        @GetMapping("/hello")
        public ResponseEntity<Resource> streamData(@RequestHeader(value = "Range", required = false) String rangeHeader){
            return ResponseEntity.ok().body(new FileSystemResource(Paths.get("D:\\MY_VIDEO.mp4")));
        }
        @GetMapping("/partial")
        public ResponseEntity<Resource> streamData2(@RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
            File file = new File(VIDEO_PATH);
            long fileLength = file.length();
            long rangeStart = 0;
            long rangeEnd = fileLength - 1;
            // Check if the Range header is present
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
                try {
                    rangeStart = Long.parseLong(ranges[0]);
//                    if (ranges.length > 1 && !ranges[1].isEmpty()) {
//                        rangeEnd = Long.parseLong(ranges[1]);
//                    }
                    rangeEnd = rangeStart + 10000000;
                    System.out.println("Ranges : startRange: "  + rangeStart + ", End Ramge:" + rangeEnd );
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().build();
                }
                // Validate ranges
                if (rangeStart < 0 || rangeEnd >= fileLength || rangeStart > rangeEnd) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                            .build();
                }
                long contentLength = rangeEnd - rangeStart + 1;
                InputStream inputStream = new FileSystemResource(file).getInputStream();
                inputStream.skip(rangeStart);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");
                headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");
                headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
                headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .headers(headers)
                        .body(new InputStreamResource(inputStream));
            }
            return ResponseEntity.ok().body(new FileSystemResource(Paths.get(VIDEO_PATH)));
        }

        @GetMapping("/partialaudio")
        public ResponseEntity<Resource> streamData3(@RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
            File file = new File(AUDIO_PATH);
            long fileLength = file.length();
            long rangeStart = 0;
            long rangeEnd = fileLength - 1;
            // Check if the Range header is present
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
                try {
                    rangeStart = Long.parseLong(ranges[0]);
//                    if (ranges.length > 1 && !ranges[1].isEmpty()) {
//                        rangeEnd = Long.parseLong(ranges[1]);
//                    }
                    rangeEnd = rangeStart + 100000;
                    System.out.println("Ranges : startRange: "  + rangeStart + ", End Ramge:" + rangeEnd );
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().build();
                }
                // Validate ranges
                if (rangeStart < 0 || rangeEnd >= fileLength || rangeStart > rangeEnd) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                            .build();
                }
                long contentLength = rangeEnd - rangeStart + 1;
                InputStream inputStream = new FileSystemResource(file).getInputStream();
                inputStream.skip(rangeStart);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, "audio/mp3");
                headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");
                headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
                headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .headers(headers)
                        .body(new InputStreamResource(inputStream));
            }
            return ResponseEntity.ok().body(new FileSystemResource(Paths.get(VIDEO_PATH)));
        }
}