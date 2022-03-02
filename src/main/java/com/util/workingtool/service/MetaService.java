package com.util.workingtool.service;


import com.util.workingtool.entity.ColumnList;
import com.util.workingtool.repository.ColumnListRepository;
import com.util.workingtool.entity.TableList;
import com.util.workingtool.repository.TableListRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MetaService {

    @Value("${meta.default-schema}")
    private String DEFAULT_SCHEMA;

    private final TableListRepository tableListRepository;
    private final ColumnListRepository columnListRepository;

    public List<TableList> getTableList(){
        return tableListRepository.findBySchema(DEFAULT_SCHEMA);
    }

    public List<ColumnList> getColList(final String tableName){
        return columnListRepository.findBySchemaAndTable(DEFAULT_SCHEMA, tableName);
    }
}
