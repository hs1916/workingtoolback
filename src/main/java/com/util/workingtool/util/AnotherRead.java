package com.util.workingtool.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.util.Iterator;

@Slf4j
public class AnotherRead {

    public void readExcelFile(String filename) throws Exception {

        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader( pkg );
        SharedStringsTable sharedStringsTable = (SharedStringsTable) r.getSharedStringsTable();
//        StylesTable stylesTable = r.getStylesTable();
        XMLReader parser = XMLHelper.newXMLReader();
        ContentHandler handler = new SheetHandler(sharedStringsTable);
        ContentHandler newHandler = new SheetHandlerSecond(sharedStringsTable);
        parser.setContentHandler(handler);
        Iterator<InputStream> sheets = r.getSheetsData();
        while(sheets.hasNext()) {
            System.out.println("Processing new sheet:\n");
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
            System.out.println("");
        }





//        SAXParserFactory factory = SAXParserFactory.newInstance();
//        SAXParser saxParser = factory.newSAXParser();



//        try (OPCPackage opcPackage = OPCPackage.open(filename);) {
//            XSSFReader xssfReader = new XSSFReader(opcPackage);
//            sharedStringsTable = (SharedStringsTable) xssfReader.getSharedStringsTable();
//            stylesTable = xssfReader.getStylesTable();
//            ContentHandler handler = this;
//            Iterator<InputStream> sheets = xssfReader.getSheetsData();
//            if (sheets instanceof XSSFReader.SheetIterator) {
//                XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) sheets;
//                while (sheetIterator.hasNext()) {
//                    try (InputStream sheet = sheetIterator.next();){
//                        sheetName = sheetIterator.getSheetName();
//                        log.info(" sheetName : {} ", sheetName);
//                        sheetNumber++;
//                        if (!processSheet()) {
//                            continue;
//                        }
//                        InputSource sheetSource = new InputSource(sheet);
//                        startSheet();
//                        saxParser.parse(sheetSource, (DefaultHandler) handler);
//                        endSheet();
//                    }
//                }
//            }
//        }
    }

    /**
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    private static class SheetHandler extends DefaultHandler {
        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;
        private SheetHandler(SharedStringsTable sst) {
            this.sst = sst;
        }
        public void startElement(String uri, String localName, String name,
                                 Attributes attributes) throws SAXException {
            // c => cell

//            log.info( " name : {}", name);
            if (name.equals("row")) {
                String rowNumStr = attributes.getValue("r");
//            log.info("ROW# : {}", rowNumStr);
//                rowNumber = Long.parseLong(rowNumStr);
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
                log.info( " CELL# - cellType : {}-{}", attributes.getValue("r"), cellType);
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
                log.info("sst lastContents --- : {}", lastContents);
            }
            // v => contents of a cell
            // Output after we've seen the string contents
            log.info("before name: {} / value : {}", name, lastContents);
            if(name.equals("v")) {
//                System.out.println();
//                System.out.println(" vvv - " + lastContents);
                log.info("name: {} / value : {}", name, lastContents);
            }
        }
        public void characters(char[] ch, int start, int length) {
            String str = new String(ch);
//            log.info(" all string {}", str);
            lastContents += new String(ch, start, length);

            log.info(" lastContents on characters : {}", lastContents);
        }
    }
}
