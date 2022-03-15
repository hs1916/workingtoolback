package com.util.workingtool.util;

import java.io.InputStream;
import java.util.*;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.ParserConfigurationException;

@Getter
@Slf4j
public class ExampleEventUserModel {

//    private   Map<String, String> header = new HashMap<>();
    private  List<Map<String, String>> resultArr = new ArrayList<Map<String, String>>();
//    private  Map<String, String> rowValues = new HashMap<>();


    public void processOneSheet(String filename) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader( pkg );
        SharedStringsTable sst = (SharedStringsTable) r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);
        // To look up the Sheet Name / Sheet Order / rID,
        //  you need to process the core Workbook stream.
        // Normally it's of the form rId# or rSheet#

        log.info(" rId 2 -----");

        InputStream sheet2 = r.getSheet("rId2");
        InputSource sheetSource = new InputSource(sheet2);
        parser.parse(sheetSource);
        sheet2.close();
        log.info(" rId 1 -----");
        sheet2 = r.getSheet("rId1");
        sheetSource = new InputSource(sheet2);
        parser.parse(sheetSource);
        sheet2.close();
        log.info(" rId 3 -----");
        sheet2 = r.getSheet("rId3");
        sheetSource = new InputSource(sheet2);
        parser.parse(sheetSource);
        sheet2.close();


    }
    public List<Map<String, String>> processAllSheets(String filename) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader( pkg );
        SharedStringsTable sst = (SharedStringsTable) r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);
        
        Iterator<InputStream> sheets = r.getSheetsData();

        while(sheets.hasNext()) {
            System.out.println("Processing new sheet:\n");
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
            System.out.println("");
        }
        return resultArr;
    }
    public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException, ParserConfigurationException {
        XMLReader parser = XMLHelper.newXMLReader();
        ContentHandler handler = new SheetHandler(sst, resultArr);
        parser.setContentHandler(handler);
        return parser;
    }



    /**
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    public static class SheetHandler extends DefaultHandler {
        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;
        private  long rowNumber = 0;
        private  String cellId;
        private  Map<String, String> header = new HashMap<>();
        private  List<Map<String, String>> allRows = new ArrayList<Map<String, String>>();
        private  Map<String, String> rowValues = new HashMap<>();

        private SheetHandler(SharedStringsTable sst, List<Map<String, String>> resultArr) {
            this.sst = sst;
            this.allRows = resultArr;
        }
        public void startElement(String uri, String localName, String name,
                                 Attributes attributes) throws SAXException {
            // c => cell

//            log.info( " name : {}", name);
            if (name.equals("row")) {
                String rowNumStr = attributes.getValue("r");
//                log.info("ROW# : {}", rowNumStr);
                rowNumber = Long.parseLong(rowNumStr);

            }
            else if(name.equals("c")) {
                // Print the cell reference

//                System.out.print(attributes.getValue("r") + " - ");
                // Figure out if the value is an index in the SST
                String cellType = attributes.getValue("t");
//                log.info("get R Value {}", attributes.getValue("r"));
//                log.debug("cellType {}", cellType);
//                log.debug("get C Value {}", attributes.getValue("c"));
//                log.debug("get V Value {}", attributes.getValue("v"));
                cellId = getColumnId(attributes.getValue("r"));
//                log.info( " CELL# - cellType : {}-{}", attributes.getValue("r"), cellType);
//                log.info(" Cell Id : {} ", getColumnId(attributes.getValue("r")));
                if(cellType != null && cellType.equals("s")) {
                    nextIsString = true;
                } else {
//                    log.info(" cellType - {} : name {} ", cellType, name);
                    nextIsString = false;
                }
            }
            // Clear contents cache
            lastContents = "";
        }
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            // Process the last contents as required.
            // Do now, as characters() may be called more than once
            if(nextIsString) {
                int idx = Integer.parseInt(lastContents);
                lastContents = sst.getItemAt(idx).getString();
                nextIsString = false;
//                log.info("sst lastContents --- : {}", lastContents);

            }
            // v => contents of a cell
            // Output after we've seen the string contents
//            log.info("before name: {} / value : {}", name, lastContents);
            if(name.equals("v") || name.equals("t")) {
//                System.out.println();
//                System.out.println(" vvv - " + lastContents);
//                log.info("name: {} / value : {}", name, lastContents);
                if (rowNumber == 1){
                    header.put(cellId, lastContents);
                } else {
                    rowValues.put(header.get(cellId), lastContents);
                }
                cellId = null;
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
        public void characters(char[] ch, int start, int length) {
            String str = new String(ch);
//            log.info(" all string {}", str);
            lastContents += new String(ch, start, length);

//            log.info(" lastContents on characters : {}", lastContents);
        }
    }
    public static void main(String[] args) throws Exception {
        ExampleEventUserModel example = new ExampleEventUserModel();
        example.processOneSheet(args[0]);
        example.processAllSheets(args[0]);
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