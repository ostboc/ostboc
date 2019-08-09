package cn.itcast.web.controller.cargo;

import cn.itcast.dao.cargo.ContractProductDao;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.vo.ContractProductVo;
import cn.itcast.web.controller.BaseController;
import cn.itcast.web.utils.DownloadUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 导出出货表
 */
@Controller
@RequestMapping("/cargo/contract")
public class OutProductController extends BaseController {

    @Reference
    private ContractProductService contractProductService;

    /**
     * 1. 进入导出页面
     * 地址：http://localhost:8080/cargo/contract/print.do
     */
    @RequestMapping("/print")
    public String print() {
        return "cargo/print/contract-print";
    }

    /**
     * 2. 导出出货表（1）普通导出： XSSF

     @RequestMapping("/printExcel1") public void printExcel1(String inputDate) throws IOException {
     // 第一步：导出第一行   大标题
     Workbook workbook = new XSSFWorkbook();
     Sheet sheet = workbook.createSheet();
     // 设置列宽
     sheet.setColumnWidth(0,256*5);
     sheet.setColumnWidth(1,256*25);
     sheet.setColumnWidth(2,256*10);
     sheet.setColumnWidth(3,256*15);
     sheet.setColumnWidth(4,256*29);
     sheet.setColumnWidth(5,256*11);
     sheet.setColumnWidth(6,256*15);
     sheet.setColumnWidth(7,256*15);
     sheet.setColumnWidth(8,256*15);

     //A.合并单元格：开始行0   结束行0    开始列1   结束列8
     sheet.addMergedRegion(new CellRangeAddress(0,0,1,8));

     //B. 创建行
     Row row = sheet.createRow(0);
     row.setHeightInPoints(36);
     //C. 创建单元格
     Cell cell = row.createCell(1);
     //D. 设置单元格内容、样式
     // 设置单元格内容.  2012-08 --> 2012年8月份出货表
     // 设置单元格内容.  2012-11 --> 2012年8月份出货表
     String result = inputDate.replaceAll("-0","-").replace("-","年") + "月份出货表";
     // 设置内容
     cell.setCellValue(result);
     // 设置样式
     cell.setCellStyle(this.bigTitle(workbook));


     // 第二步：导出第二行   表头
     //A. 创建第二行
     row = sheet.createRow(1);
     row.setHeightInPoints(26);
     //B. 导出第二行的列
     String titles[] = {"客户","订单号","货号","数量","工厂","工厂交期","船期","贸易条款"};
     for (int i = 0; i<titles.length; i++){
     cell = row.createCell(i+1);
     cell.setCellValue(titles[i]);
     cell.setCellStyle(this.title(workbook));
     }

     // 第三步：导出数据行   调service
     List<ContractProductVo> list =
     contractProductService.findByShipTime(getLoginCompanyId(), inputDate);
     if (list != null && list.size()>0){
     int index = 2;
     for (ContractProductVo cpVo : list) {
     // 创建每一行
     row = sheet.createRow(index++);
     row.setHeightInPoints(24);

     // 创建列
     cell = row.createCell(1);
     cell.setCellValue(cpVo.getCustomName());
     cell.setCellStyle(this.text(workbook));

     cell = row.createCell(2);
     cell.setCellValue(cpVo.getContractNo());
     cell.setCellStyle(this.text(workbook));

     cell = row.createCell(3);
     cell.setCellValue(cpVo.getProductNo());
     cell.setCellStyle(this.text(workbook));

     cell = row.createCell(4);
     if (cpVo.getCnumber() != null) {
     cell.setCellValue(cpVo.getCnumber());
     }
     cell.setCellStyle(this.text(workbook));

     cell = row.createCell(5);
     cell.setCellValue(cpVo.getFactoryName());
     cell.setCellStyle(this.text(workbook));

     cell = row.createCell(6);
     cell.setCellValue(cpVo.getDeliveryPeriod());
     cell.setCellStyle(this.text(workbook));

     cell = row.createCell(7);
     cell.setCellValue(cpVo.getShipTime());
     cell.setCellStyle(this.text(workbook));

     cell = row.createCell(8);
     cell.setCellValue(cpVo.getTradeTerms());
     cell.setCellStyle(this.text(workbook));
     }
     }

     // 第四步：导出下载
     DownloadUtil downloadUtil = new DownloadUtil();
     ByteArrayOutputStream bos = new ByteArrayOutputStream();
     // 把excel文件流，写入bos缓冲流
     workbook.write(bos);
     downloadUtil.download(bos,response,"出货表.xlsx");

     workbook.close();
     }
     */

    /**
     * 3. 导出出货表（2）模板导出： 适用于HSSF、XSSF
     */
    @RequestMapping("/printExcel2")
    public void printExcel2(String inputDate) throws IOException {
        // 获取excel模板文件流
        InputStream in =
                session.getServletContext()
                        .getResourceAsStream("/make/xlsprint/tOUTPRODUCT.xlsx");

        // 第一步：导出第一行   大标题
        Workbook workbook = new XSSFWorkbook(in);
        // 【获取工作表】
        Sheet sheet = workbook.getSheetAt(0);

        // 【获取行】
        Row row = sheet.getRow(0);
        // 【获取单元格】
        Cell cell = row.getCell(1);
        //设置单元格内容
        String result = inputDate.replaceAll("-0", "-").replace("-", "年") + "月份出货表";
        // 设置内容
        cell.setCellValue(result);


        // 第二步：获取第三行每一个单元格的样式
        // 【获取第三行】
        row = sheet.getRow(2);
        // 定义样式数组
        CellStyle[] cellStyles = new CellStyle[8];
        for (int i = 0; i < cellStyles.length; i++) {
            cell = row.getCell(i + 1);
            cellStyles[i] = cell.getCellStyle();
        }

        // 第三步：导出数据行   调service
        List<ContractProductVo> list =
                contractProductService.findByShipTime(getLoginCompanyId(), inputDate);
        if (list != null && list.size() > 0) {
            int index = 2;
            for (ContractProductVo cpVo : list) {
                // 创建每一行
                row = sheet.createRow(index++);

                // 创建列
                cell = row.createCell(1);
                cell.setCellValue(cpVo.getCustomName());
                cell.setCellStyle(cellStyles[0]);

                cell = row.createCell(2);
                cell.setCellValue(cpVo.getContractNo());
                cell.setCellStyle(cellStyles[1]);

                cell = row.createCell(3);
                cell.setCellValue(cpVo.getProductNo());
                cell.setCellStyle(cellStyles[2]);

                cell = row.createCell(4);
                if (cpVo.getCnumber() != null) {
                    cell.setCellValue(cpVo.getCnumber());
                }
                cell.setCellStyle(cellStyles[3]);

                cell = row.createCell(5);
                cell.setCellValue(cpVo.getFactoryName());
                cell.setCellStyle(cellStyles[4]);

                cell = row.createCell(6);
                cell.setCellValue(cpVo.getDeliveryPeriod());
                cell.setCellStyle(cellStyles[5]);

                cell = row.createCell(7);
                cell.setCellValue(cpVo.getShipTime());
                cell.setCellStyle(cellStyles[6]);

                cell = row.createCell(8);
                cell.setCellValue(cpVo.getTradeTerms());
                cell.setCellStyle(cellStyles[7]);
            }
        }

        // 第四步：导出下载
        DownloadUtil downloadUtil = new DownloadUtil();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 把excel文件流，写入bos缓冲流
        workbook.write(bos);
        downloadUtil.download(bos, response, "出货表.xlsx");

        workbook.close();
    }


    /**
     * 4. 导出出货表（3） SXSSF 测试百万数据的导出
     */
    @RequestMapping("/printExcel")
    public void printExcel(String inputDate) throws IOException {
        // 第一步：导出第一行   大标题
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 设置列宽
        sheet.setColumnWidth(0, 256 * 5);
        sheet.setColumnWidth(1, 256 * 25);
        sheet.setColumnWidth(2, 256 * 10);
        sheet.setColumnWidth(3, 256 * 15);
        sheet.setColumnWidth(4, 256 * 29);
        sheet.setColumnWidth(5, 256 * 11);
        sheet.setColumnWidth(6, 256 * 15);
        sheet.setColumnWidth(7, 256 * 15);
        sheet.setColumnWidth(8, 256 * 15);

        //A.合并单元格：开始行0   结束行0    开始列1   结束列8
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 8));

        //B. 创建行
        Row row = sheet.createRow(0);
        row.setHeightInPoints(36);
        //C. 创建单元格
        Cell cell = row.createCell(1);
        //D. 设置单元格内容、样式
        // 设置单元格内容.  2012-08 --> 2012年8月份出货表
        // 设置单元格内容.  2012-11 --> 2012年8月份出货表
        String result = inputDate.replaceAll("-0", "-").replace("-", "年") + "月份出货表";
        // 设置内容
        cell.setCellValue(result);
        // 设置样式
        cell.setCellStyle(this.bigTitle(workbook));


        // 第二步：导出第二行   表头
        //A. 创建第二行
        row = sheet.createRow(1);
        row.setHeightInPoints(26);
        //B. 导出第二行的列
        String titles[] = {"客户", "订单号", "货号", "数量", "工厂", "工厂交期", "船期", "贸易条款"};
        for (int i = 0; i < titles.length; i++) {
            cell = row.createCell(i + 1);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(this.title(workbook));
        }

        // 第三步：导出数据行   调service
        List<ContractProductVo> list =
                contractProductService.findByShipTime(getLoginCompanyId(), inputDate);
        if (list != null && list.size() > 0) {
            int index = 2;
            for (ContractProductVo cpVo : list) {
                for (int i=1; i<=10000; i++) {
                    // 创建每一行
                    row = sheet.createRow(index++);
                    row.setHeightInPoints(24);

                    // 创建列
                    cell = row.createCell(1);
                    cell.setCellValue(cpVo.getCustomName());

                    cell = row.createCell(2);
                    cell.setCellValue(cpVo.getContractNo());

                    cell = row.createCell(3);
                    cell.setCellValue(cpVo.getProductNo());

                    cell = row.createCell(4);
                    if (cpVo.getCnumber() != null) {
                        cell.setCellValue(cpVo.getCnumber());
                    }

                    cell = row.createCell(5);
                    cell.setCellValue(cpVo.getFactoryName());

                    cell = row.createCell(6);
                    cell.setCellValue(cpVo.getDeliveryPeriod());

                    cell = row.createCell(7);
                    cell.setCellValue(cpVo.getShipTime());

                    cell = row.createCell(8);
                    cell.setCellValue(cpVo.getTradeTerms());
                }
            }
        }

        // 第四步：导出下载
        DownloadUtil downloadUtil = new DownloadUtil();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 把excel文件流，写入bos缓冲流
        workbook.write(bos);
        downloadUtil.download(bos, response, "出货表.xlsx");

        workbook.close();
    }


    //大标题的样式
    public CellStyle bigTitle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);//字体加粗
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);                //横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        return style;
    }

    //小标题的样式
    public CellStyle title(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);                //横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        style.setBorderTop(BorderStyle.THIN);                        //上细线
        style.setBorderBottom(BorderStyle.THIN);                    //下细线
        style.setBorderLeft(BorderStyle.THIN);                        //左细线
        style.setBorderRight(BorderStyle.THIN);                        //右细线
        return style;
    }

    //文字样式
    public CellStyle text(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 10);

        style.setFont(font);

        style.setAlignment(HorizontalAlignment.LEFT);                //横向居左
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        style.setBorderTop(BorderStyle.THIN);                        //上细线
        style.setBorderBottom(BorderStyle.THIN);                    //下细线
        style.setBorderLeft(BorderStyle.THIN);                        //左细线
        style.setBorderRight(BorderStyle.THIN);                        //右细线

        return style;
    }
}
