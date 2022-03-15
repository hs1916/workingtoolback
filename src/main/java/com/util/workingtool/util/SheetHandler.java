package com.util.workingtool.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Getter
public class SheetHandler extends DefaultHandler {

    private SharedStringsTable sharedStringsTable;
    private String contents;
    private boolean isCellValue;
    private boolean fromSST;

    protected String sheetName;
    protected int sheetNumber = 0;
    protected Map<String, String> header = new HashMap<>();
    protected List<Map<String, String>> allRows = new ArrayList<Map<String, String>>();
    protected Map<String, String> rowValues = new HashMap<>();
    protected StylesTable stylesTable;
    protected Map<String, CTXf> rowStyles = new HashMap<>();
    protected long rowNumber = 0;
    protected String cellId;

    public SheetHandler(SharedStringsTable sst, StylesTable stylesTable) {
        this.sharedStringsTable = sst;
//        this.stylesTable = stylesTable;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
//        super.startElement(uri, localName, qName, attributes);
        // Clear contents cache
        contents = "";

        log.info(" name : {} ", name);
        if (name.equals("row")) { // element row represents row
            String rowNumStr = attributes.getValue("r");
            log.info("ROW# : {}", rowNumStr);
            rowNumber = Long.parseLong(rowNumStr);
        } else if (name.equals("c")) { // element c represents Cell

            cellId = getColumnId(attributes.getValue("r"));
            // attribute r represents the cell reference ex. 셀번호
//            log.info( " CELL# : {}", attributes.getValue("r"));
            // attribute t represents the cell type
            String cellType = attributes.getType("t");
            log.info( " CELL# - cellType : {}-{}", attributes.getValue("r"), cellType);
            if (cellType != null && cellType.equals("s")) {
                // cell type s means value will be extracted from SharedStringsTable
                fromSST = true;
                isCellValue = true;
            } else {
                fromSST = false;
            }
            String cellStyleStr = attributes.getValue("s");
            if (cellStyleStr != null) {
                int cellStyleInt = Integer.parseInt(cellStyleStr);
                CTXf cellStyle = stylesTable.getCellStyleXfAt(cellStyleInt);
                rowStyles.put(cellId, cellStyle);
            }
        }
        else if (name.equals("v")) { // value
            isCellValue = true;
        }
        else if (name.equals("t")) { // text
            isCellValue = true;
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        String str = new String(ch);
//        log.info(" all string {}", str);
        if (isCellValue) {
            contents += new String(ch, start, length);
            log.info(" lastContents on characters : {}", contents);
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {

        log.info(" fromSST {} ", fromSST);

        if (fromSST) {
            int index = Integer.parseInt(contents);
            contents = new XSSFRichTextString(sharedStringsTable.getItemAt(index).getString()).toString();
            log.info(" get Value fromSST {} - {} - {}", rowNumber, cellId, contents);
            if (rowNumber == 1){
                header.put(cellId, contents);
            } else {
                rowValues.put(header.get(cellId), contents);
            }
            cellId = null;
            isCellValue = false;
            fromSST = false;
        } else if (isCellValue) {
            if (rowNumber == 1){
                header.put(cellId, contents);
            } else {
                rowValues.put(header.get(cellId), contents);
            }
            log.info(" get Value {} - {} - {}", rowNumber, cellId, contents);
            isCellValue = false;
        } else if (name.equals("row")) {
            if (rowNumber == 1) {

            }
            Map<String, String> insertData = new HashMap<>();
            insertData.putAll(rowValues);
            if (rowNumber > 1) {
                allRows.add(insertData);
            }
            rowValues.clear();;

        }
    }

    protected boolean processSheet() {
        return true;
    }

    protected void startSheet() {

    }

    protected void endSheet() {

    }

    protected void processRow() {

    }

    protected static String getColumnId(String attribute) throws SAXException {
        for (int i = 0; i < attribute.length(); i++) {
            if (!Character.isAlphabetic(attribute.charAt(i))) {
                return attribute.substring(0, i);
            }
        }
        throw new SAXException("Invalid format " + attribute);
    }




}
