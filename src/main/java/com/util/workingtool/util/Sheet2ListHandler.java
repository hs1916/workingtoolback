package com.util.workingtool.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.binary.XSSFBSheetHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
//import org.apache.poi.xssf.binary.XSSFBSheetHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.util.List;

@Slf4j
@Getter
@Setter
public class Sheet2ListHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

    private boolean firstCellRow = false;
    private int currentCol = -1;

    //  Collection 객체
    private List<String[]> rows;
    // Collection 에 추가될 row
    private String[] row;
    // 컬럼 카운트
    private int columnCnt;
    // cell 이벤트 처리시
    private int currColNum = 0;

    public Sheet2ListHandler (List<String[]> rows, int columnCnt){

        log.info(" --- Sheet2Handler Constructor ");

        this.rows = rows;
        this.columnCnt = columnCnt;
    }


    @Override
    public void startRow(int rowNum) {
        this.row = new String[columnCnt];
        currColNum = 0;
    }

    @Override
    public void endRow(int rowNum) {
        boolean addFlag = false;
        for (String data: row) {
            if (!"".equals(data)){
                addFlag = true;
            }
        }
        System.out.println(" ---------------------------------------- ");
        System.out.println(row);
        System.out.println(" ---------------------------------------- ");

        if (addFlag) rows.add(row);

    }

    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
        int thisCol = (new CellReference(cellReference)).getCol();
        int missedCols = thisCol - currentCol -1 ;

        for (int i = 0; i < missedCols; i++) {
            row[currColNum++] = "";
        }

        currentCol = thisCol;
        row[currColNum++] = formattedValue == null ? "" : formattedValue;
    }

    @Override
    public void headerFooter(String text, boolean isHeader, String tagName) {
//        XSSFSheetXMLHandler.SheetContentsHandler.super.headerFooter(text, isHeader, tagName);
    }

    @Override
    public void endSheet() {
//        XSSFSheetXMLHandler.SheetContentsHandler.super.endSheet();
    }
}
