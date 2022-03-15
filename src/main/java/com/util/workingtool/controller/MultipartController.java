package com.util.workingtool.controller;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.util.workingtool.dto.ResponseDTO;
import com.util.workingtool.service.ExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class MultipartController {
    @Value("${file.upload-path}") private String UPLOAD_PATH;
    private final ExcelService excelService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadFiles(@RequestPart(value = "multipartFiles")  MultipartFile[] multipartFiles,
                                              @RequestPart(value = "tableName") String tableName) throws JsonProcessingException {

        try {
            List<Map<String, String>> list = excelService.uploadData(multipartFiles, UPLOAD_PATH, tableName);
            ResponseDTO<Map<String, String>> response = ResponseDTO.<Map<String, String>>builder().data(list).build();
//            ResponseDTO<Map<Object, Object>> response = null;
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<Map<Object, Object>> response = ResponseDTO.<Map<Object, Object>>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }


    }
}