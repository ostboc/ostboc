package cn.itcast.service.system;

import cn.itcast.domain.system.Role;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface RoleService {

    // 分页查询部门
    PageInfo<Role> findByPage(String companyId, int pageNum, int pageSize);

    // 主键查询
    Role findById(String id);
    // 添加
    void save(Role role);

    // 修改
    void update(Role role);

    // 删除
    void delete(String id);

    // 角色分配权限
    void updateRoleModule(String roleId, String moduleIds);

    //查询所有角色
    List<Role> findAll(String loginCompanyId);

    // 查询用户已经拥有的角色
    List<Role> findUserRoleByUserId(String id);
}
