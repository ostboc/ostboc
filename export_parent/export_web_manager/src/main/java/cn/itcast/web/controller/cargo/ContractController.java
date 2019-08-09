package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractExample;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/cargo/contract")
public class ContractController extends BaseController{

    // 注入购销合同service服务接口
    @Reference
    private ContractService contractService;

    /**
     * 1. 分页列表
     */
    @RequestMapping("/list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize){
        // 构造查询条件对象
        ContractExample example = new ContractExample();
        example.setOrderByClause("create_time desc");
        // 根据所属企业查询其下的购销合同
        ContractExample.Criteria criteria = example.createCriteria();
        criteria.andCompanyIdEqualTo(getLoginCompanyId());

        /**
         * 购销合同列表实现细粒度权限控制，根据当前登陆用户等级显示购销合同。
         * 用户等级：degree
         *   0-saas管理员
         *   1-企业管理员
         *   2-管理所有下属部门和人员
         *   3-管理本部门
         *   4-普通员工
         */
        // 获取用户等级
        Integer degree = getLoginUser().getDegree();
        if (degree == 4){
            //4-普通员工, 只能查看自己创建的购销合同
            // SELECT * FROM co_contract WHERE create_by=''
            criteria.andCreateByEqualTo(getLoginUser().getId());
        }
        else if (degree == 3){
            //3-管理本部门, 根据登陆用户的部门id查询
            //SELECT * FROM co_contract WHERE create_dept=''
            criteria.andCreateDeptEqualTo(getLoginUser().getDeptId());
        }
        else if (degree == 2){
            //2-管理所有下属部门和人员
            PageInfo<Contract> pageInfo =
                    contractService.selectByDeptId(getLoginUser().getDeptId(), pageNum, pageSize);
            // 返回
            ModelAndView mv = new ModelAndView();
            mv.addObject("pageInfo",pageInfo);
            mv.setViewName("cargo/contract/contract-list");
            return mv;
        }

        // 调用service分页查询
        PageInfo<Contract> pageInfo =
                contractService.findByPage(example,pageNum,pageSize);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("cargo/contract/contract-list");
        return mv;
    }

    /**
     * 2. 进入添加页面
     */
    @RequestMapping("/toAdd")
    public String toAdd(){
        return "cargo/contract/contract-add";
    }

    /**
     * 3. 添加或修改
     */
    @RequestMapping("/edit")
    public String edit(Contract contract){
        contract.setCompanyId(getLoginCompanyId());
        contract.setCompanyName(getLoginCompanyName());
        // 判断
        if (StringUtils.isEmpty(contract.getId())){
            /*需求：需要根据登陆用户显示当前登陆用户创建的购销合同。*/
            // 设置创建人
            contract.setCreateBy(getLoginUser().getId());
            // 设置创建人部门
            contract.setCreateDept(getLoginUser().getDeptId());
            // 添加
            contractService.save(contract);
        } else {
            // 修改
            contractService.update(contract);
        }
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 4. 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        // 回显用户数据
        Contract contract = contractService.findById(id);
        request.setAttribute("contract",contract);
        return "cargo/contract/contract-update";
    }

    /**
     * 5. 删除
     */
    @RequestMapping("/delete")
    public String delete(String id){
        contractService.delete(id);
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 5. 提交: 购销合同状态改为1
     */
    @RequestMapping("/submit")
    public String submit(String id){
        // 构建对象
        Contract contract = new Contract();
        contract.setId(id);
        contract.setState(1);
        // 动态更新： 根据id修改状态
        contractService.update(contract);
        return "redirect:/cargo/contract/list.do";
    }
    /**
     * 6. 取消：购销合同状态改为0
     */
    @RequestMapping("/cancel")
    public String cancel(String id){
        // 构建对象
        Contract contract = new Contract();
        contract.setId(id);
        contract.setState(0);
        // 动态更新： 根据id修改状态
        contractService.update(contract);
        return "redirect:/cargo/contract/list.do";
    }

}
