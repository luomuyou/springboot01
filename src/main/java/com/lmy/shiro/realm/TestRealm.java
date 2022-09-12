package com.lmy.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

public class TestRealm extends AuthorizingRealm {

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //获得身份信息
        String principal = (String) authenticationToken.getPrincipal();
        System.out.println("此时的用户名principal为：" + principal);
        System.out.println("此时的用户名this.getName()为：" + this.getName());

        //根据数据库查询对比身份信息，先根据用户名等到用户的信息，密码、salt等才能进行下一步的处理对比
        if ("lq".equals(principal)) {
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(
                    "lq",
                    "027517873e8fd0992b942af48f41bca1",
                    ByteSource.Util.bytes("7]eG6mWb"),
                    this.getName());
            return simpleAuthenticationInfo;         //认证通过,返回SimpleAuthenticationInfo对象
        }
        return null;
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获得身份信息
        String primaryPrincipal = (String) principalCollection.getPrimaryPrincipal();
        //查询数据库中用户的角色表，得到结果集合，遍历集合给用户赋予角色信息
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        //给 "admin"这个角色
        simpleAuthorizationInfo.addRole("user");
        //simpleAuthorizationInfo.addRole("admin");
        //再给，update更新和find查询操作，
        simpleAuthorizationInfo.addStringPermission("user:update:*");  //只对01用户实例
        simpleAuthorizationInfo.addStringPermission("user:*:02");
        return simpleAuthorizationInfo;
    }
}

