package cn.itcast.dao.system;

import cn.itcast.domain.system.Dept;

import java.util.List;

public interface DeptDao {
    // 根据企业id查询部门
    List<Dept> findAll(String companyId);

    // 根据部门id查询
    Dept findById(String id);

    // 添加
    void save(Dept dept);

    // 修改
    void update(Dept dept);

    // 根据父部门查询
    List<Dept> findDeptByParentId(String id);

    // 删除
    void delete(String id);
}
