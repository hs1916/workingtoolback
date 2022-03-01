package com.util.workingtool.controller;


import com.util.workingtool.service.MetaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/meta")
public class MetaController {

    private final MetaService metaService;

    @GetMapping("/table/list")
    public ResponseEntity<?> getTableList(){
        return ResponseEntity.ok().body(metaService.getTableList());
    }

    @GetMapping("/col/list")
    public ResponseEntity<?> getColumnList(@RequestParam String tableName){
        return ResponseEntity.ok().body(metaService.getColList(tableName));
    }
}
