package cn.itcast.service.system;

import cn.itcast.domain.system.Module;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ModuleService {

    // 分页查询
    PageInfo<Module> findByPage(int pageNum, int pageSize);

    // 主键查询
    Module findById(String id);

    // 添加
    void save(Module module);

    // 修改
    void update(Module module);

    // 删除
    void delete(String id);

    List<Module> findAll();

    //查询角色已经具有的权限
    List<Module> findRoleModulesByRoleId(String roleId);

    // 根据用户id查询权限
    List<Module> findModuleByUserId(String id);
}
