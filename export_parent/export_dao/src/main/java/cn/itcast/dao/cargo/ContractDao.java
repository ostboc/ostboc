package cn.itcast.dao.cargo;

import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractExample;

import java.util.List;

public interface ContractDao {

	//删除
    int deleteByPrimaryKey(String id);

	//保存
    int insertSelective(Contract record);

	//条件查询
    List<Contract> selectByExample(ContractExample example);

	//id查询
    Contract selectByPrimaryKey(String id);

	//更新
    int updateByPrimaryKeySelective(Contract record);

    // 根据部门查询购销合同(查询当前部门以及所有子部门的员工创建的购销合同)
    List<Contract> findByDept(String deptId);
}