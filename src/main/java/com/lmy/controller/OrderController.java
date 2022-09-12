package com.lmy.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/order")
public class OrderController {

    /*
    * 编程式授权
    * 认证之后测试这个条件，是否有权限，业务逻辑代码实现
    * */
    @RequestMapping("save")
    public String save(){
        Subject subject = SecurityUtils.getSubject();
        System.out.println("subject:"+subject);
        if(subject.hasRole("admin")){        //判断角色为admin才可以访问这个资源
            System.out.println("保存订单");
        }else{
            System.out.println("无权操作");
        }
        return "index";
    }

    /*
    注解式授权
     */
    @RequestMapping("save1")
    @RequiresRoles(value = {"user","admin"})    //判断当前角色同时有user和admin权限才可以访问这个方法
    @RequiresPermissions("user:update:01")  //有这个权限才可以进来
    public String save1(){
        System.out.println("进入方法");
        return "index";
    }
}
