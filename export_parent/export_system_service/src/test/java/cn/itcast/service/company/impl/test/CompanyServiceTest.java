package cn.itcast.service.company.impl.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext-*.xml")
public class CompanyServiceTest {

    /*
    @Autowired
    private CompanyService companyService;

    @Test
    public void update(){
        Company company = new Company();
        company.setId("99449e56-dd3f-4edd-8b14-9a72c466a6f6");
        company.setName("福耀玻璃厂");
        company.setExpirationDate(new Date());
        companyService.update(company);
    }

    @Test
    public void findByPage(){
        PageInfo<Company> pageInfo = companyService.findByPage(2, 2);
        System.out.println(pageInfo);
    }
    */
}
