package com.lmy.controller;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lmy.entity.User;
import com.lmy.service.UserService;
import com.lmy.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/JWT")
@Slf4j
public class JWTController {
    @Autowired
    private UserService userService;


    @RequestMapping("JWTlogin")
    @ResponseBody
    public Map<String,Object> login(User user){
        log.info("用户名：",user.getUsername());
        log.info("密码：",user.getPassword());
        Map<String, Object> map = new HashMap<>();   //返回前端的登录状态信息
        Map<String, String> payload = new HashMap<>();  //存入token中的信息
        try {
            User userDB = userService.login(user);
            if(userDB != null){
                //Token令牌生成
                System.out.println("userDB.getId() "+userDB.getId());
                payload.put("id",userDB.getId());
                payload.put("userName",user.getUsername());
                String token = JWTUtils.getToken(payload);
                //状态
                map.put("state",true);
                map.put("msg","认证成功");
                //记录第一次请求获得的token令牌，返回到前端中作下一次的校验
                map.put("token",token);
            }
        }catch (Exception e){
            map.put("state",false);
            map.put("msg",e.getMessage());
        }
        return map;
    }
    /***
     * 业务层实现拦截器的效果，验证二次访问之后的token是否一致判断，
     *一般放在拦截器中处理该功能
     */
    @RequestMapping("JWTtest")
    @ResponseBody
    public Map<String,Object> testCheck(HttpServletRequest request){
       /* Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token1 = new UsernamePasswordToken("lq","lq");
        try {
            //1、用户认证，会调用CustomerRealm中设置得认证规则【1】
            subject.login(token1);
        } catch (UnknownAccountException e) {
            e.printStackTrace();
            System.out.println("用户名错误");
        } catch (IncorrectCredentialsException e) {
            e.printStackTrace();
            System.out.println("密码错误");
        };
*/
        //检查令牌，通过传入token的方式，一般toke保存在浏览器的本地存储中每次发送后台去请求的时候一起传过来验证
        String token = request.getHeader("token");    //也可以通过请求头的方式传递token令牌
        log.info("当前的token为：",token);
        Map<String, Object> map = new HashMap<>();
        try {
            JWTUtils.vertify(token);//验证token
            map.put("state",true);
            map.put("msg","请求成功");
            System.out.println();
            DecodedJWT tokenInfo = JWTUtils.getTokenInfo(token);   //从token中获取一些信息
            String payload = tokenInfo.getPayload();
            String id = tokenInfo.getClaim("id").asString();
            String userName = tokenInfo.getClaim("userName").asString();
            User user = new User();
            user.setId(id);
            user.setUsername(userName);
            System.out.println("testCheck"+user);

            User login = userService.login(user);//用户名和密码登录，然后获得全部信息
            if(login != null){
                map.put("user",login);
            }
            return map;
        }catch (SignatureVerificationException e){  //签名异常
            map.put("msg","token无效签名");
            e.printStackTrace();
        }catch (TokenExpiredException e){  //过期异常
            map.put("msg","token已过期");
            e.printStackTrace();
        }
        map.put("state",false);
        return map;
    }
}
