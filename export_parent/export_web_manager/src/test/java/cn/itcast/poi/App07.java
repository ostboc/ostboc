package cn.itcast.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class App07 {

    // 导出excel： 写入
    @Test
    public void export() throws Exception {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        // 创建工作表
        Sheet sheet = workbook.createSheet("Hello");
        // 创建行 (第一行)
        Row row = sheet.createRow(0);
        // 创建单元格 (第二列)
        Cell cell = row.createCell(1);
        // 设置单元格内容
        cell.setCellValue("Hello POI!");
        // 导出
        workbook.write(new FileOutputStream("e:\\test.xlsx"));
        workbook.close();
    }

    // 导入excel: 读取
    @Test
    public void importExcel() throws Exception {
        // 根据excel文件流，创建工作簿
        Workbook workbook = new XSSFWorkbook(new FileInputStream("e:\\test.xlsx"));
        // 获取工作表
        Sheet sheet = workbook.getSheetAt(0);

        // 获取第一行
        Row row = sheet.getRow(0);
        // 获取第一行第二列
        Cell cell = row.getCell(1);
        // 获取单元格内容
        System.out.println("单元格内容"+ cell.getStringCellValue());
        System.out.println("总行数：" + sheet.getPhysicalNumberOfRows());
        System.out.println("总列数:" + row.getPhysicalNumberOfCells());

        workbook.close();
    }



    // 读取excel模板，设置样式
    @Test
    public void importExcel2() throws Exception {
        // 根据excel文件流，创建工作簿
        Workbook workbook = new HSSFWorkbook(new FileInputStream("e:\\test.xls"));
        // 获取工作表
        Sheet sheet = workbook.getSheetAt(0);

        // 获取第一行
        Row row = sheet.getRow(0);
        // 获取第一行第二列
        Cell cell = row.getCell(1);
        cell.setCellValue("Hello,ApachePOI!");

        workbook.write(new FileOutputStream("e:\\test.xls"));
        workbook.close();
    }
}
