package com.util.workingtool.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.util.workingtool.config.JdbcTemplateConfig;
import com.util.workingtool.domain.column.ColumnList;
import com.util.workingtool.domain.column.ColumnListRepository;
import com.util.workingtool.domain.table.Sample1;
import com.util.workingtool.domain.table.Sample1Repository;
import com.util.workingtool.domain.table.TableList;
import com.util.workingtool.domain.table.TableListRepository;
import com.util.workingtool.util.ExcelReadUtil;
import com.util.workingtool.util.UploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExcelService {

    private final Sample1Repository repository;
    private final TableListRepository tableListRepository;
    private final ColumnListRepository columnListRepository;

    @Autowired
    private JdbcTemplateConfig config;

    public List<Map<Object, Object>> getExcelData(String path, String fileName) throws JsonProcessingException {
        ObjectMapper obm = new ObjectMapper();
        List<Map<Object, Object>> result = ExcelReadUtil.readExcel(path, fileName);

        ListIterator iterator = result.listIterator();

        while (iterator.hasNext()){
            Map<Object, Object> data = (Map<Object, Object>) iterator.next();
            Sample1 sample1 = obm.convertValue(data, Sample1.class);

            log.debug("col1: {}", sample1.getCol1());
            repository.save(sample1);

        }


        String jsonStr = obm.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        log.debug(jsonStr);
        return result;
    }

    public List<Map<Object, Object>> uploadData(MultipartFile[] multipartFiles, final String UPLOAD_PATH) throws JsonProcessingException {

        Map<String, String> fileInfo = UploadUtil.uploadFile(multipartFiles, UPLOAD_PATH);
        List<Map<Object, Object>> result =
                ExcelReadUtil.readExcel(UPLOAD_PATH, fileInfo.get("originName") +"."+ fileInfo.get("fileExtension"));
        ListIterator iterator = result.listIterator();
        ObjectMapper obm = new ObjectMapper();
        while (iterator.hasNext()){
            Map<Object, Object> data = (Map<Object, Object>) iterator.next();
            Sample1 sample1 = obm.convertValue(data, Sample1.class);

            log.debug("col1: {}", sample1.getCol1());
            repository.save(sample1);

        }

        List<Sample1> load = config.select("aa");

        ListIterator itr = load.listIterator();
        while (itr.hasNext()) {
            Sample1 rs = (Sample1) itr.next();
            log.debug(" 1{}, 2{}, 3{}, 4{}, 5{}", rs.getCol1(), rs.getCol2(), rs.getCol3(), rs.getCol4(), rs.getCol5());

        }


//        List<TableList> resultList = tableListRepository.findBySchema("upload");
//        ListIterator ii = resultList.listIterator();
//        while (ii.hasNext()) {
//            TableList tableList = (TableList) ii.next();
//            log.debug(" TableList : {}", tableList.getTableName());
//
//        }

        List<ColumnList> resultListCol = columnListRepository.findBySchemaAndTable("upload", "sample");
        ListIterator iis = resultListCol.listIterator();
        while (iis.hasNext()) {
            ColumnList columnList = (ColumnList) iis.next();
//            log.debug(" TableList : {}", tableList.getTableName());

        }




        return result;
    }





}
