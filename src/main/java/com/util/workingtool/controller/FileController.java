package com.util.workingtool.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/fff")
@Slf4j
public class FileController {

    @Value("${file.upload-path}") private String tempPath;

    @PostMapping("/up")
    public String upload(@RequestParam String msg, @RequestParam MultipartFile[] files) throws IOException {
        log.info("Upload start : {}", msg);
        log.debug("path : {} ", tempPath);
        for (MultipartFile file : files) {
            File tmp = new File(tempPath + UUID.randomUUID().toString());
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(), tmp);
            } catch (IOException e) {
                log.error("Error while copying.", e);
                throw e;
            }
        }
        return "success";
    }
}