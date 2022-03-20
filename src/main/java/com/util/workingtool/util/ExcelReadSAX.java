package com.util.workingtool.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.springframework.stereotype.Controller;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Controller
@Getter
@Setter
public class ExcelReadSAX {

    private List<Map<String, String>> resultArr = new ArrayList<Map<String, String>>();

    /**
     * 시트 1개 Read
     * @param filename
     * @throws Exception
     */
    public void processOneSheet(String filename) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader( pkg );
        SharedStringsTable sst = (SharedStringsTable) r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);

        InputStream sheet2 = r.getSheet("rId2");
        InputSource sheetSource = new InputSource(sheet2);
        parser.parse(sheetSource);
        sheet2.close();
    }

    public List<Map<String, String>> processAllSheets(String filename) throws Exception {

        try (OPCPackage pkg = OPCPackage.open(filename)){
            XSSFReader r = new XSSFReader( pkg );
            SharedStringsTable sst = (SharedStringsTable) r.getSharedStringsTable();
            XMLReader parser = fetchSheetParser(sst);

            Iterator<InputStream> sheets = r.getSheetsData();

            while(sheets.hasNext()) {
                log.debug("Processing new sheet:\n");
                InputStream sheet = sheets.next();
                InputSource sheetSource = new InputSource(sheet);
                parser.parse(sheetSource);
                sheet.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            log.debug(" Finally ");
        }
        return resultArr;
    }

    public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException, ParserConfigurationException {
        XMLReader parser = XMLHelper.newXMLReader();
        ContentHandler handler = new SheetHandler(sst, resultArr);
        parser.setContentHandler(handler);
        return parser;
    }

    public class SheetHandler extends DefaultHandler {
        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;
        private String rowNumStr;
        private long rowNumber = 0;
        private String cellId;
        private Map<String, String> header = new HashMap<>();
        private Map<String, String> rowValues = new HashMap<>();
        private List<Map<String, String>> allRows = new ArrayList<Map<String, String>>();

        private SheetHandler (SharedStringsTable sst, List<Map<String, String>> resultArr) {
            this.sst = sst;
            this.allRows = resultArr;
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

            String cellType;

            log.debug(" name {} / rowNumStr {} / lastContents {}", name, rowNumStr, lastContents);

            if(name.equals("row")) { // excel row #
                rowNumStr = attributes.getValue("r");
                rowNumber = Long.parseLong(rowNumStr);
            } else if (name.equals("c")) { // cell references
                cellType = attributes.getValue("t");
                cellId = getColumnId(attributes.getValue("r"));
                if (cellType != null && cellType.equals("s")) {
                    nextIsString = true; // from styledSheet
                } else {
                    nextIsString = false;
                }
            }
            // Clear contents cache
            lastContents = "";
        }

        @Override
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            // Process the last contents as required.
            // Do now, as characters() may be called more than once
            if(nextIsString) {
                int idx = Integer.parseInt(lastContents);
                lastContents = sst.getItemAt(idx).getString();
                nextIsString = false;
                log.debug("sst lastContents --- : {}", lastContents);
            }

            // v => contents of a cell
            // Output after we've seen the string contents
            if(name.equals("v") || name.equals("t")) {
                if (rowNumber == 1){ // 1st row -> header
                    header.put(cellId, lastContents);
                } else {
                    rowValues.put(header.get(cellId), lastContents);
                }
                cellId = null;
            } else if (name.equals("row")) {
                if (rowNumber == 1 ) {
                    // headder -> Todo
                }
                Map<String, String> insertData = new HashMap<>();
                insertData.putAll(rowValues);
                if (rowNumber > 1) {
                    allRows.add(insertData);
                }
                rowValues.clear();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            String str = new String(ch);
            lastContents += new String(ch, start, length);
            log.debug(" all string  : {}", str);
            log.debug(" fetched Str : {}", lastContents);
        }
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
