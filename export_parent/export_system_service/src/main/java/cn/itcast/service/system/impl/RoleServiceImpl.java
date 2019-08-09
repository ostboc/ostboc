package cn.itcast.service.system.impl;

import cn.itcast.dao.system.RoleDao;
import cn.itcast.domain.system.Role;
import cn.itcast.service.system.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    // 注入dao
    @Autowired
    private RoleDao roleDao;

    @Override
    public PageInfo<Role> findByPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(roleDao.findAll(companyId));
    }

    @Override
    public void save(Role role) {
        // 设置主键
        role.setId(UUID.randomUUID().toString());
        // 保存
        roleDao.save(role);
    }

    @Override
    public void update(Role role) {
        roleDao.update(role);
    }

    @Override
    public Role findById(String id) {
        return roleDao.findById(id);
    }

    @Override
    public void delete(String id) {
        roleDao.delete(id);
    }

    @Override
    public void updateRoleModule(String roleId, String moduleIds) {
        //-- 需求：给角色分配权限 （给角色添加权限或取消角色的权限）
        //-- 1) 先解除角色权限的关系
        //DELETE FROM pe_role_module WHERE role_id=''
        //-- 2) 给角色添加权限
        //INSERT INTO pe_role_module(role_id,module_id)VALUES('','')

        //1) 先解除角色权限的关系
        roleDao.deleteRoleModuleByRoleId(roleId);

        //2) 给角色添加权限
        if (moduleIds != null && !"".equals(moduleIds)){
            //A. 分割字符串
            String[] array = moduleIds.split(",");
            //B. 判断
            if (array != null && array.length > 0){
                for (String moduleId : array) {
                    //C. 角色权限中间表添加数据
                    roleDao.saveRoleModule(roleId,moduleId);
                }
            }
        }
    }

    @Override
    public List<Role> findAll(String loginCompanyId) {
        return roleDao.findAll(loginCompanyId);
    }

    @Override
    public List<Role> findUserRoleByUserId(String id) {
        return roleDao.findUserRoleByUserId(id);
    }
}
