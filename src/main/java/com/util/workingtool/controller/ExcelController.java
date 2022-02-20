package com.util.workingtool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.util.workingtool.dto.ExcelFileDTO;
import com.util.workingtool.dto.ResponseDTO;
import com.util.workingtool.service.ExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/excel")
public class ExcelController {


    private final ExcelService excelService;

    @GetMapping("/health")
    public String healthCheck(){
        return "healty";
    }

    @GetMapping("/show")
    public ResponseEntity<?> getExcelData() {
//    public ResponseEntity<?> getExcelData(@RequestBody ExcelFileDTO excelFileDto) {
        try {

            ObjectMapper obm = new ObjectMapper();

//            log.debug("path {} / fileName {}", excelFileDto.getPath(), excelFileDto.getFileName());


            String path = "/Users/heechanshin/Downloads/";
            String fileName = "test.xlsx";

            List<Map<Object, Object>> list =
                    excelService.getExcelData(path, fileName);
//                    excelService.getExcelData(excelFileDto.getPath(), excelFileDto.getFileName());
            String result = obm.writerWithDefaultPrettyPrinter().writeValueAsString(list);
//            ResponseDTO<String> response = ResponseDTO.<String>builder().data(Collections.singletonList(result)).build();
            return ResponseEntity.ok().body(list);
        } catch ( Exception e ) {
            String error = e.getMessage();
            ResponseDTO<ExcelFileDTO> response = ResponseDTO.<ExcelFileDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }



}
