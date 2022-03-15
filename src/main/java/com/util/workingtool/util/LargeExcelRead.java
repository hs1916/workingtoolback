package com.util.workingtool.util;

import com.util.workingtool.entity.ColumnList;
import com.util.workingtool.service.MetaService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.springframework.stereotype.Controller;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Controller
public class LargeExcelRead {

    private final MetaService metaService;
    private String tableName;
    private List<ColumnList> tableColList;


    public List<String[]> largeExcelRead(String path, String fileName, String tableName) throws OpenXML4JException, IOException, SAXException, ParserConfigurationException {

        setTableName(tableName);
        setTableColList(metaService.getColList(tableName));

        File excelFile = new File( path + fileName );


        OPCPackage opc = OPCPackage.open(excelFile);
        XSSFReader xssfReader = new XSSFReader(opc);
        XSSFReader.SheetIterator itr = (XSSFReader.SheetIterator) xssfReader.getSheetsData();

        StylesTable styles = xssfReader.getStylesTable();
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(opc);

        List<String[]> dataList = new ArrayList<String[]>();

        while (itr.hasNext()) {
            InputStream sheetstream = itr.next();
            InputSource sheetSource = new InputSource(sheetstream);

            Sheet2ListHandler sheet2ListHandler = new Sheet2ListHandler(dataList, tableColList.size());

            ContentHandler contentHandler = new XSSFSheetXMLHandler(styles, strings, sheet2ListHandler, false);

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();

            XMLReader sheetParser = saxParser.getXMLReader();
            sheetParser.setContentHandler(contentHandler);

            sheetParser.parse(sheetSource);

            sheetstream.close();
        }

        opc.close();

        return dataList;
    }

}
