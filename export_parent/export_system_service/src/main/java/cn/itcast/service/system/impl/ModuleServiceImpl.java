package cn.itcast.service.system.impl;

import cn.itcast.dao.system.ModuleDao;
import cn.itcast.dao.system.UserDao;
import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ModuleServiceImpl implements ModuleService {

    // 注入dao
    @Autowired
    private ModuleDao moduleDao;
    @Autowired
    private UserDao userDao;

    @Override
    public PageInfo<Module> findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(moduleDao.findAll());
    }

    @Override
    public void save(Module module) {
        // 设置主键
        module.setId(UUID.randomUUID().toString());
        // 保存
        moduleDao.save(module);
    }

    @Override
    public void update(Module module) {
        moduleDao.update(module);
    }

    @Override
    public Module findById(String id) {
        return moduleDao.findById(id);
    }

    @Override
    public void delete(String id) {
        moduleDao.delete(id);
    }

    @Override
    public List<Module> findAll() {
        return moduleDao.findAll();
    }

    @Override
    public List<Module> findRoleModulesByRoleId(String roleId) {
        return moduleDao.findRoleModulesByRoleId(roleId);
    }

    /**
     -- 登陆用户是：Saas管理员
     SELECT * FROM ss_module WHERE belong=0

     -- 登陆用户是：企业管理员
     SELECT * FROM ss_module WHERE belong=1

     -- 登陆用户的等级为其他
     SELECT * FROM ss_module WHERE module_id IN (
     SELECT DISTINCT(rm.module_id) FROM pe_role_user ru, pe_role_module rm
     WHERE ru.role_id=rm.role_id AND ru.user_id=''
     )
     */
    @Override
    public List<Module> findModuleByUserId(String id) {
        // 根据用户id查询
        User user = userDao.findById(id);
        // 获取用户的登陆
        Integer degree = user.getDegree();

        List<Module> moduleList = null;
        // 根据用户登陆判断
        if (degree == 0){
            // Saas管理员
            moduleList = moduleDao.findByBelong(0);
        }
        else if (degree == 1){
            // 企业管理员
            moduleList = moduleDao.findByBelong(1);
        }
        else {
            // 其他用户（根据用户id查询角色权限）
            moduleList = moduleDao.findModuleByUserId(id);
        }

        return moduleList;
    }
}
