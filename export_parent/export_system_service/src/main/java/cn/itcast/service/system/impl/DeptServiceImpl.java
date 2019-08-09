package cn.itcast.service.system.impl;

import cn.itcast.dao.system.DeptDao;
import cn.itcast.domain.system.Dept;
import cn.itcast.service.system.DeptService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeptServiceImpl implements DeptService {

    // 注入dao
    @Autowired
    private DeptDao deptDao;

    @Override
    public List<Dept> findAll(String companyId) {
        return deptDao.findAll(companyId);
    }

    @Override
    public PageInfo<Dept> findByPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(deptDao.findAll(companyId));
    }

    @Override
    public void save(Dept dept) {
        // 设置主键
        dept.setId(UUID.randomUUID().toString());
        // 保存
        deptDao.save(dept);
    }

    @Override
    public void update(Dept dept) {
        deptDao.update(dept);
    }

    @Override
    public Dept findById(String id) {
        return deptDao.findById(id);
    }

    @Override
    public boolean delete(String id) {
        //1） 先查询
        List<Dept> list = deptDao.findDeptByParentId(id);

        //2） 判断
        if (list != null && list.size() > 0){
            //当前部门下有子部门, 不能删除
            return false;
        } else {
            // 调用dao删除
            deptDao.delete(id);
            return true;
        }
    }
}
