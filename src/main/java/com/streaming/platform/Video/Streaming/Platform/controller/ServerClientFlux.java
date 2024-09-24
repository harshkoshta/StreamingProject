package com.streaming.platform.Video.Streaming.Platform.controller;

import com.streaming.platform.Video.Streaming.Platform.StreamingConstant;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class ServerClientFlux {
    @GetMapping("/media")
    public ResponseEntity<ResourceRegion> getMedia(@RequestHeader HttpHeaders httpHeaders) {
        Resource media = new FileSystemResource(StreamingConstant.VIDEO_PATH);
        ResourceRegion region = resourceRegion(media, httpHeaders);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(media)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }
    private ResourceRegion resourceRegion(Resource media, HttpHeaders headers) {
        long contentLength = 0;
        try {
            contentLength = media.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<HttpRange> range = headers.getRange();
        if (!range.isEmpty()) {
            String[] ranges = range.get(0).toString().split("-");
            long start = Long.parseLong(ranges[0]);
            long end = start+1000000;
            if (ranges.length>1 && end > contentLength) {
                end=contentLength;
            }
            long rangeLength = Math.min(end - start, contentLength);
            return new ResourceRegion(media, start, rangeLength);
        } else {
            long rangeLength = Math.min(1024 * 1024, contentLength);
            return new ResourceRegion(media, 0, rangeLength);
        }
    }
}
