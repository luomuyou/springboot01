package com.lmy.controller;

import com.lmy.entity.User;
import com.lmy.service.UserService;
import com.lmy.utils.JWTUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    //private RedisTemplate<Object,Object> redisTemplate;
    private RedisTemplate<Object,Object> redisTemplate;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "register")      //注册用户
    public String register(User user){
        try {
            System.out.println("register UserCtroller:"+user);
            System.out.println("redisTemplate:"+redisTemplate);
            userService.register(user);
            return "redirect:/login.html";
        }catch (Exception e){
            e.printStackTrace();
            return "redirect:/register.html";
        }
    }

    @RequestMapping(value = "login")
    public String login(Model model, String username, String password){
        //可以直接使用安全管理工具类获得主体，ShiroConfig配置类中设置得安全管理对象已经注入了
        Subject subject = SecurityUtils.getSubject();
        System.out.println(subject);
        Map<String, String> payload = new HashMap<>();
        //类似把用户名和密码包装起来准备拿去认证
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        try {
            //1、用户认证，会调用CustomerRealm中设置得认证规则【1】
            subject.login(token);
            //shiro认证通过之后，返回Token

        } catch (UnknownAccountException e) {
            e.printStackTrace();
            System.out.println("用户名错误");
            return "redirect:/login.html";
        } catch (IncorrectCredentialsException e) {
            e.printStackTrace();
            System.out.println("密码错误");
            return "redirect:/login.html";
        };
        model.addAttribute("username",username);
        return "index";
    }
    /*
    退出登录，redis缓存中该用户的认证和授权信息都删除了，登录之后还是得去数据库中查询匹对，
        如果不退出直接有刷新登录，此时得redis缓存中又该用户认证和授权信息，但是反序列化匹对得时候又不成功？
     */
    @RequestMapping("logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();   //退出用户认证
        return "redirect:/login.html";
    }
    @RequestMapping("index.html")
    public String index(){
        return "index";
    }

}
