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
    private final ExcelReadSAX readSAX;

    @Autowired
    private JdbcTemplateConfig config;

//    public List<Map<Object, Object>> uploadData(MultipartFile[] multipartFiles, final String UPLOAD_PATH, final String tableName) throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {
    public List<Map<String, String>> uploadData(MultipartFile[] multipartFiles, final String UPLOAD_PATH, final String tableName) throws Exception {
        Map<String, String> fileInfo = UploadUtil.uploadFile(multipartFiles, UPLOAD_PATH);


        Long startTime = TimeUtil.checkTime(System.currentTimeMillis());
        List<Map<String, String>> resultValue =
                readSAX.processAllSheets(UPLOAD_PATH + fileInfo.get("originName") +"."+ fileInfo.get("fileExtension"));

        int[] resultArr = config.batchInsert(resultValue, tableName);

        Long endTime = TimeUtil.checkTime(System.currentTimeMillis());
        Long diffTimeInsec = (endTime - startTime) / 1000;
        log.info( " upload Duration Time {} ", diffTimeInsec);
        return resultValue;
    }
}
