package cn.itcast.service.system;

import cn.itcast.domain.system.User;
import com.github.pagehelper.PageInfo;

public interface UserService {

    // 分页查询部门
    PageInfo<User> findByPage(String companyId, int pageNum, int pageSize);

    // 主键查询
    User findById(String id);
    // 添加
    void save(User user);

    // 修改
    void update(User user);

    // 删除
    boolean delete(String id);

    // 用户分配角色
    void changeRole(String userId, String[] roleIds);

    // 根据email查询
    User findByEmail(String email);
}
