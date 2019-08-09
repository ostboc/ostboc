package cn.itcast.dao.cargo;

import cn.itcast.domain.cargo.Factory;
import cn.itcast.domain.cargo.FactoryExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext-*.xml")
public class FactoryDaoTest {
    // 注入dao，测试逆向工程生产的数据访问方法
    @Autowired
    private FactoryDao factoryDao;

    /**
     * 1. 普通的更新： 更新所有的字段
     * update co_factory set ctype = ?, full_name = ?, factory_name = ?,
     * contacts = ?, phone = ?, mobile = ?, fax = ?, address = ?,
     * inspector = ?, remark = ?, order_no = ?, state = ?,
     * create_by = ?, create_dept = ?, create_time = ?, update_by = ?,
     * update_time = ? where id = ?
     */
    @Test
    public void update(){
        Factory factory = new Factory();
        factory.setId("1d3125df-b186-4a2d-8048-df817d30e52b");
        factory.setFactoryName("草原三兄弟");
        factory.setCreateTime(new Date());
        factory.setUpdateTime(new Date());
        //factoryDao.updateByPrimaryKey(factory);
    }
    /**
     * 2. 动态更新： 根据对象属性如果有值才更新。
     * update co_factory SET contacts = ? where id = ?
     */
    @Test
    public void update2(){
        Factory factory = new Factory();
        factory.setId("9");
        factory.setContacts("史建国");
        factoryDao.updateByPrimaryKeySelective(factory);
    }

    @Test
    public void insert(){
        //factoryDao.insert(null);
        //factoryDao.insertSelective(null);
    }

    @Test
    public void find(){
        // select * from co_factory  不带条件
        //List<Factory> factoryList = factoryDao.selectByExample(null);
        //System.out.println(factoryList);

        // 构造条件对象
        //select * from co_factory WHERE ( factory_name = ? )
        FactoryExample example = new FactoryExample();
        FactoryExample.Criteria criteria = example.createCriteria();
        criteria.andFactoryNameEqualTo("汇越");
        List<Factory> factoryList = factoryDao.selectByExample(example);
        System.out.println(factoryList);
    }

}
