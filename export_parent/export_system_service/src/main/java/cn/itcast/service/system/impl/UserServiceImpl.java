package cn.itcast.service.system.impl;

import cn.itcast.dao.system.UserDao;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    // 注入dao
    @Autowired
    private UserDao userDao;

    @Override
    public PageInfo<User> findByPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(userDao.findAll(companyId));
    }

    @Override
    public void save(User user) {
        // 设置主键
        user.setId(UUID.randomUUID().toString());
        if (user.getPassword() != null){
            // 加密加盐，把用户名作为盐
            String encodePwd = new Md5Hash(user.getPassword(),user.getEmail()).toString();
            user.setPassword(encodePwd);
        }
        // 保存
        userDao.save(user);
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Override
    public User findById(String id) {
        return userDao.findById(id);
    }

    @Override
    public boolean delete(String id) {
        //1) 根据用户的id查询用户角色中间表
        Long count = userDao.findUserRoleByUserId(id);

        //2) 如果查询到数据就不能删除； 否则可以删除
        if (count > 0){
            // 说明当前删除的用户有被用户角色中间表引用，不能删除
            return false;
        } else {
            userDao.delete(id);
            return true;
        }
    }

    @Override
    public void changeRole(String userId, String[] roleIds) {
        //-- 用户分配角色
        //-- 1）先解除用户角色的关系
        //DELETE FROM pe_role_user WHERE user_id=''
        //-- 2) 给用户添加角色
        //INSERT INTO pe_role_user(user_id,role_id)VALUES('','')

        //1）先解除用户角色的关系
        userDao.deleteUserRoleByUserId(userId);
        //2) 给用户添加角色
        if (roleIds != null && roleIds.length > 0){
            for (String roleId : roleIds) {
                userDao.saveUserRole(userId,roleId);
            }
        }
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }
}
