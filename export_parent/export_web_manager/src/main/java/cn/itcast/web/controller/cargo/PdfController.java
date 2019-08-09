package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.Export;
import cn.itcast.domain.cargo.ExportProduct;
import cn.itcast.domain.cargo.ExportProductExample;
import cn.itcast.domain.system.User;
import cn.itcast.service.cargo.ExportProductService;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.web.controller.BaseController;
import cn.itcast.web.utils.BeanMapUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.collections.Buffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@Controller
@RequestMapping("/cargo/export")
public class PdfController extends BaseController {
    /**
     * 导出pdf（1） Hello，PDF！
     * 功能入口： 出口报运单，点击下载
     * 访问地址： http://localhost:8080/cargo/export/exportPdf.do?id=0
     */
    @RequestMapping("/exportPdf1")
    public void exportPdf1() throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test01.jasper");

        //2. 通过JrPrint往模板中填充数据
        // 参数1：jasper文件流
        // 参数2：通过map集合往模板中填充数据。map的key要与模板中的参数一致。现在为空的map。
        // 参数3：通过数据源填充数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, new HashMap<>(), new JREmptyDataSource());

        //3. 导出pdf模板
        OutputStream out = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);
        out.close();
    }

    /**
     * 导出pdf（2） 设置字体后的模板文件
     */
    @RequestMapping("/exportPdf2")
    public void exportPdf2() throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test02.jasper");

        //2. 通过JrPrint往模板中填充数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, new HashMap<>(), new JREmptyDataSource());

        //3. 导出pdf模板
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    /**
     * 导出pdf（3） 设置字体后的模板文件
     */
    @RequestMapping("/exportPdf3")
    public void exportPdf3() throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test03_parameter_map.jasper");

        // 构造map，map中的key与模板中的parameter参数名称一致
        Map<String, Object> map = new HashMap<>();
        map.put("userName", "杰克");
        map.put("email", "jack@itcast.cn");
        map.put("companyName", "Java学院");
        map.put("deptName", "大神云集部门");

        //2. 通过JrPrint往模板中填充数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, map, new JREmptyDataSource());

        //3. 导出pdf模板
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    /**
     * 导出pdf（4） 数据源填充 A JDBC数据源作为模板数据
     */
    // 注入连接池
    @Autowired
    private DataSource dataSource;

    @RequestMapping("/exportPdf4")
    public void exportPdf4() throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test04_jdbc.jasper");

        //2. 通过JrPrint往模板中填充数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, new HashMap<>(), dataSource.getConnection());

        //3. 导出pdf模板
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    /**
     * 导出pdf（5） 数据源填充 A JavaBean作为数据源
     */
    @RequestMapping("/exportPdf5")
    public void exportPdf5() throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test05_javabean.jasper");

        // 构造list集合
        List<User> list = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            User user = new User();
            user.setUserName("纯情杰杰" + i);
            user.setEmail("jj@itcast.cn");
            user.setDeptName("主播界");
            user.setCompanyName("IT黑马");
            list.add(user);
        }

        // 构造javabean数据源
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

        //2. 通过JrPrint往模板中填充数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, new HashMap<>(), dataSource);

        //3. 导出pdf模板
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    /**
     * 导出pdf（6） 分组报表  javabean数据源
     */
    @RequestMapping("/exportPdf6")
    public void exportPdf6() throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test06_group.jasper");

        // 构造list集合
        List<User> list = new ArrayList<>();
        for (int j = 1; j < 3; j++) {
            for (int i = 1; i < 6; i++) {
                User user = new User();
                user.setUserName("纯情杰杰" + i);
                user.setEmail("jj@itcast.cn");
                user.setDeptName("主播界");
                user.setCompanyName("IT野马" + j);
                list.add(user);
            }
        }

        // 构造javabean数据源
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

        //2. 通过JrPrint往模板中填充数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, new HashMap<>(), dataSource);

        //3. 导出pdf模板
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    /**
     * 导出pdf（7） 饼图  javabean数据源
     */
    @RequestMapping("/exportPdf7")
    public void exportPdf7() throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test07_chart.jasper");

        // 构造list集合
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("title","厂家"+i);
            map.put("value",new Random().nextInt(100));
            list.add(map);
        }

        // 构造javabean数据源
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

        //2. 通过JrPrint往模板中填充数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, new HashMap<>(), dataSource);

        //3. 导出pdf模板
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }



    /**
     * 导出pdf（8） 实现出口报运单pdf导出
     * 访问地址： http://localhost:8080/cargo/export/exportPdf.do?id=0
     * 请求参数： 报运单id
     * 需求分析：
     *      1. 根据报运单id，查询报运单信息。      map
     *      2. 根据报运单id，查询其下所有商品。    list
     */
    @Reference
    private ExportService exportService;
    @Reference
    private ExportProductService exportProductService;

    @RequestMapping("/exportPdf")
    @ResponseBody
    public void exportPdf(String id) throws Exception {
        //1. 加载test01.jasper文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/export.jasper");

        //第一步：构造map数据：  报运单
        Export export = exportService.findById(id);
        // 对象转换为map
        Map<String, Object> map = BeanMapUtils.beanToMap(export);

        //第二步：构造list数据： 商品
        ExportProductExample epExample = new ExportProductExample();
        epExample.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> list = exportProductService.findAll(epExample);

        // 构造javabean数据源
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

        //2. 通过JrPrint往模板中填充数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in, map, dataSource);

        //3. 导出pdf模板
        response.setContentType("application/pdf;charset=UTF-8");
        // 下载响应头
        response.addHeader("Content-Disposition","attachment;filename=export.pdf");
        ServletOutputStream out = response.getOutputStream();
        // 导出pdf到response输出流，这样浏览器就可以下载了
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        out.close();
    }
}
