package com.util.workingtool.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.apache.poi.hssf.usermodel.HSSFCell;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RunWith(SpringRunner.class)
public class NumberConvert {

    String exponentialNumber = "7.471750146731201E8";
    String dateVal = "44469";


    @Test
    public void convertNumber() {
        String token = "";
        Integer direction = 1;
        if (exponentialNumber.contains("E+")) {
            token = "E+";
        }else if (exponentialNumber.contains("E-")) {
            token = "E-";
            direction = -1;
        }else if (exponentialNumber.contains("E")) {
            token = "E";
        }
        String[] sep = exponentialNumber.split(token);
        Double cal = Double.parseDouble(sep[0]) * Math.pow(10,Double.parseDouble(sep[1])) * direction;
    }

    @Test
    public void convertDate() throws ParseException {

        Calendar cal = Calendar.getInstance();
//        Date date = new Date((long) Double.parseDouble(dateVal));
//        System.out.println("current: " + df.format(cal.getTime()));
//        cal.setTime(cal.setTime(df.parse("2019-07-04")));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        cal.setTime(df.parse("1900-01-01"));
//        System.out.println("current1: " + df.format(cal));
        System.out.println("current2: " + df.format(cal.getTime()));

//        cal.add(Calendar.MONTH, 2);
        System.out.println("---------------");
        System.out.println(Float.parseFloat(dateVal));


        cal.add(Calendar.DATE, (int) Long.parseLong(dateVal) );
        System.out.println("after: " + df.format(cal.getTime()));
        System.out.println("---------------");


        Date myDate = new Date();
        HSSFCell myCell = null;
// code that assigns a cell from an HSSFSheet to 'myCell' would go here...
        myCell.setCellValue((int) Long.parseLong(dateVal) );

        System.out.println(myCell.toString());

    }



    @Test
    public void excelDate() {
        Workbook wb = new HSSFWorkbook();
//Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("new sheet");
// Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow(0);
// Create a cell and put a date value in it.  The first cell is not styled
// as a date.
        Cell cell = row.createCell(0);
        cell.setCellValue(Integer.parseInt(dateVal));
        Date date = cell.getDateCellValue();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        System.out.println("aaaa");
        System.out.println(format.format(date)); // 20090529

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(df.format(date));

    }
}
