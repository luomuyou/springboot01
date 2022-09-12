package com.lmy.JWTInterceptor;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmy.utils.JWTUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class JWTInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*登录之后确认好，最初的生成的token返回到浏览器中，
        之后的每次请求都要从浏览器中把token放到请求头中，给后代浏览器确认，前后的token一致才可放行*/
        Map<String, Object> map = new HashMap<>();
        String token = request.getHeader("token");  //获取二次请求头中的token信息
        System.out.println("请求头："+token);
        try {
            JWTUtils.vertify(token);    //验证token
            map.put("state",true);
            map.put("msg","请求成功");
            return true;    //没有出异常，放行请求
        }catch (SignatureVerificationException e){  //签名异常
            map.put("msg","token无效签名");
            e.printStackTrace();
        }catch (TokenExpiredException e){  //过期异常
            map.put("msg","token已过期");
            e.printStackTrace();
        }catch (Exception e){  //总异常
            map.put("msg","其他异常.token无效");
            e.printStackTrace();
        }

        map.put("state",false);
        //map信息以json的格式返回给前端页面知晓，用Jackson这个依赖
        /*ObjectMapper是Jackson库中主要用于读取和写入Json数据的类，能够很方便地将Java对象转为Json格式的数据，
        用于后端Servlet向AJAX传递Json数据，动态地将数据展示在页面上*/
        String json = new ObjectMapper().writeValueAsString(map);
        //指定转化回去的格式
        response.setContentType("application/json;Charset=UTF-8");
        response.getWriter().println(json);
        //response.sendRedirect("login.html");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
