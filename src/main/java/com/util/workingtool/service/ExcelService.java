package com.util.workingtool.service;

import com.util.workingtool.config.JdbcTemplateConfig;
import com.util.workingtool.repository.Sample1Repository;
import com.util.workingtool.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExcelService {

    private final ExcelReadUtil readUtil;
    private final Sample1Repository repository;
    private final LargeExcelRead largeExcelRead;

    @Autowired
    private JdbcTemplateConfig config;

//    public List<Map<Object, Object>> uploadData(MultipartFile[] multipartFiles, final String UPLOAD_PATH, final String tableName) throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {
    public List<Map<String, String>> uploadData(MultipartFile[] multipartFiles, final String UPLOAD_PATH, final String tableName) throws Exception {
        Map<String, String> fileInfo = UploadUtil.uploadFile(multipartFiles, UPLOAD_PATH);


        Long startTime = System.currentTimeMillis();
        Date date = new Date();
        date.setTime(startTime);
        String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        log.info(" start this time : {} ", sdf.format(date));

        log.info(" start at time {}", startTime);

//        AnotherRead handler = new AnotherRead();
//
//
////
//        handler.readExcelFile(UPLOAD_PATH + fileInfo.get("originName") +"."+ fileInfo.get("fileExtension"));
//        List<Map<String, String>> resultValue = handler.getAllRows();

//        log.info(rowValues.toString());


        ExampleEventUserModel example = new ExampleEventUserModel();
//        example.processOneSheet(UPLOAD_PATH + fileInfo.get("originName") +"."+ fileInfo.get("fileExtension"));
        List<Map<String, String>> resultValue = example.processAllSheets(UPLOAD_PATH + fileInfo.get("originName") +"."+ fileInfo.get("fileExtension"));


//        List<String[]> resultValue = largeExcelRead.largeExcelRead(UPLOAD_PATH, fileInfo.get("originName") +"."+ fileInfo.get("fileExtension"), tableName);

//        List<Map<Object, Object>> resultValue =
//                readUtil.readExcel(UPLOAD_PATH, fileInfo.get("originName") +"."+ fileInfo.get("fileExtension"), tableName);


//        int[] resultArr = config.batchInsert(resultValue, tableName);
        Long endTime = System.currentTimeMillis();
//
        Long diffTimeInsec = (endTime - startTime) / 1000;
//
        log.info( " upload Duration Time {} ", diffTimeInsec);
        Date endDate = new Date();
        endDate.setTime(endTime);
        log.info(" End this time : {} ", sdf.format(endDate));


        return resultValue;
    }
}
