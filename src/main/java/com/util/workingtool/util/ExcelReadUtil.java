package com.util.workingtool.util;

import com.util.workingtool.domain.ColumnList;
import com.util.workingtool.service.MetaService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * POI lIB
 */
@Controller
@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public class ExcelReadUtil {

    private final MetaService metaService;
    private String tableName;


    public List<Map<Object, Object>> readExcel(String path, String fileName, String tableName) {

        List<Map<Object, Object>> list = new ArrayList<>();
        this.setTableName(tableName);

        if(path == null || fileName == null){
            return list;
        }

        FileInputStream is = null;
        File excel = new File(path + fileName);
        try {
            is = new FileInputStream(excel);
            Workbook workbook = null;

            if(fileName.endsWith(".xls")){
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith("xlsx")){
                workbook = new XSSFWorkbook(is);
            }

            if (workbook != null) {
                int sheets = workbook.getNumberOfSheets();
                getSheet(workbook, sheets, list);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public void getSheet(Workbook workbook, int sheets, List<Map<Object, Object>> list){
        for (int z = 0; z < sheets; z++ ){
            Sheet sheet = workbook.getSheetAt(z);
            int rows = sheet.getLastRowNum();
            getRow(sheet, rows, list);
        }
    }

    public void getRow(Sheet sheet, int rows, List<Map<Object, Object>> list) {
        for (int i = 0; i <= rows; i++ ){
            Row row = sheet.getRow(i);
            if (row != null) {
                int cells = row.getPhysicalNumberOfCells();
                list.add(getCell(row, cells));
            }
        }
    }


    public Map<Object, Object> getCell(Row row, int cells) {
        List<ColumnList> colList = metaService.getColList(getTableName());

        for (int i = 0; i < colList.size(); i++) {
            log.debug("col {} - {} ", i, colList.get(i).getColumnName());
        }

        Map<Object, Object> map = new HashMap<>();
        for( int j = 0; j< cells; j++) {
            if (j >= colList.size()){
                break;
            }
            Cell cell = row.getCell(j);
            if (cell != null) {
                switch (cell.getCellType()) {
                    case BLANK:
                        map.put( colList.get(j).getColumnName(), "");
                        break;
                    case STRING:
                        map.put( colList.get(j).getColumnName(), cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            map.put( colList.get(j).getColumnName(), cell.getDateCellValue());
                        } else {
                            map.put( colList.get(j).getColumnName(), cell.getNumericCellValue());
                        }
                        break;
                    case ERROR:
                        map.put( colList.get(j).getColumnName(), cell.getErrorCellValue());
                        break;
                    case FORMULA:
                        map.put( colList.get(j).getColumnName(), cell.getCellFormula());
                        break;
                    default:
                        map.put( colList.get(j).getColumnName(), "");
                        break;
                }
            }
        }
        return map;
    }

}
