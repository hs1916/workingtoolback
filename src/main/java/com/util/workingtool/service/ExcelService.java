package com.util.workingtool.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.util.workingtool.config.JdbcTemplateConfig;
import com.util.workingtool.repository.Sample1Repository;
import com.util.workingtool.util.ExcelReadUtil;
import com.util.workingtool.util.UploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExcelService {

    private final ExcelReadUtil readUtil;
    private final Sample1Repository repository;

    @Autowired
    private JdbcTemplateConfig config;

    public List<Map<Object, Object>> uploadData(MultipartFile[] multipartFiles, final String UPLOAD_PATH, final String tableName) throws JsonProcessingException {
        Map<String, String> fileInfo = UploadUtil.uploadFile(multipartFiles, UPLOAD_PATH);
        List<Map<Object, Object>> resultValue =
                readUtil.readExcel(UPLOAD_PATH, fileInfo.get("originName") +"."+ fileInfo.get("fileExtension"), tableName);

//        boolean result = config.updateList(resultValue, tableName);

        int[] resultArr = config.batchInsert(resultValue, tableName);

        //        ListIterator iterator = result.listIterator();
//        ObjectMapper obm = new ObjectMapper();
//        while (iterator.hasNext()){
//            Map<Object, Object> data = (Map<Object, Object>) iterator.next();
//            Sample1 sample1 = obm.convertValue(data, Sample1.class);
//
//            log.debug("col1: {}", sample1.getCol1());
//            repository.save(sample1);
//
//        }
        return resultValue;
    }
}
