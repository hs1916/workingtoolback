package com.util.workingtool.controller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebMvcTest(controllers = HelloController.class)
public class HelloControllerTest {

//    @Autowired
//    private MockMvc mvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void returnHello() throws Exception {


        String hello = "hello";

        //when
        String body = this.restTemplate.getForObject("/api/hello", String.class);

        assertThat(body).isEqualTo(hello);

    }

}
