package cn.itcast.service.system;

import cn.itcast.domain.system.Dept;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface DeptService {
    // 根据企业id查询部门
    List<Dept> findAll(String companyId);

    // 分页查询部门
    PageInfo<Dept> findByPage(String companyId,int pageNum,int pageSize);

    // 添加
    void save(Dept dept);

    // 修改
    void update(Dept dept);

    // 主键查询
    Dept findById(String id);

    // 删除
    boolean delete(String id);
}
