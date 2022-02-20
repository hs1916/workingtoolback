package com.util.workingtool.controller;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.util.workingtool.service.ExcelService;
import com.util.workingtool.util.UploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class MultipartController {
    @Value("${file.upload-path}") private String UPLOAD_PATH;
    private final ExcelService excelService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadFiles(@RequestParam MultipartFile[] multipartFiles) throws JsonProcessingException {

        List<Map<Object, Object>> list = excelService.uploadData(multipartFiles, UPLOAD_PATH);

        return ResponseEntity.ok().body(list);

    }
}