package com.util.workingtool.controller;


import com.google.gson.Gson;
import com.util.workingtool.entity.ColumnList;
import com.util.workingtool.service.MetaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MetaControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaControllerTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MetaService metaService;

    private final Gson gson = new Gson();

    @Test
    public void colNameList() {

        String table = "sample";
        String URL = "/api/col/list/"+table;

        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(URL, String.class);

        List<ColumnList> colList = metaService.getColList(table);
        String jsonResultFromDB = gson.toJson(colList);

        assertThat(responseEntity.getBody()).isEqualTo(jsonResultFromDB);

    }

}
