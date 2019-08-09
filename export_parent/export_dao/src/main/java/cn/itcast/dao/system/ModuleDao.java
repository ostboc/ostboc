package cn.itcast.dao.system;

import cn.itcast.domain.system.Module;

import java.util.List;

public interface ModuleDao {
    //查询全部
    List<Module> findAll();

    //根据id查询
    Module findById(String moduleId);

    //根据id删除
    void delete(String moduleId);

    //添加
    void save(Module module);

    //更新
    void update(Module module);

    //查询角色已经具有的权限
    List<Module> findRoleModulesByRoleId(String roleId);

    // 根据belong查询
    List<Module> findByBelong(int belong);

    // 根据用户id查询角色权限
    List<Module> findModuleByUserId(String userId);
}