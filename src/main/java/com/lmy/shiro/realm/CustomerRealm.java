package com.lmy.shiro.realm;

import com.lmy.dao.UserDao;
import com.lmy.entity.Perm;
import com.lmy.entity.Role;
import com.lmy.entity.User;
import com.lmy.service.UserService;
import com.lmy.shiro.salt.MyByteSource;
import com.lmy.shiro.salt.MySimpleByteSource;
import com.lmy.utils.ApplicationContextUtils;
import com.lmy.utils.ByteSourceUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class CustomerRealm extends AuthorizingRealm {
    /*@Autowired    //CustomerRealm并没有注入容器中，为什么还是可以用@Autowired获得容器中的对象给UserDao呢？
    private UserDao userDao;*/

    /*
    将认证和授权的数据源从数据库中取出进行判断
     */

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //获得身份信息
        String principal = (String) authenticationToken.getPrincipal();
        System.out.println("此时的用户名principal为：" + principal);

        UserDao userDao = (UserDao)ApplicationContextUtils.getBean("userDao"); //直接获得工厂容器中的dao层的bean操作数据库
        System.out.println("ssssssssssss:"+userDao);
        User user = userDao.findByUserName(principal);

        //根据数据库查询对比身份信息，先根据用户名等到用户的信息，密码、salt等才能进行下一步的处理对比
        if (user.getUsername().equals(principal)) {
            /*
            SimpleAuthenticationInfo类校验，传入下面得构造方法中
            principal：正确的用户名
            user.getPassword()：密码明文
            MyByteSource.Util.bytes(user.getSalt()) ：密码的MD5加密salt盐 处理,
                自定义盐  序列化存入redis缓存时需要处理一下这个salt,由于这个ByteSource.Util类没有实现序列化接口
            this.getName()表示realm的名字，无需关心
                     MyByteSource.Util.bytes(user.getSalt())直接用这个会报错,
                     ByteSourceUtils.bytes(user.getSalt()),用这个封装一下不会报错
                     new MySimpleByteSource(user.getSalt()),加上反序列化设置之后
             */
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(
                    principal,
                    //传入数据库中的Password和Salt通过定义的认证规程(MD5+散列次数)是否等于前端输入的明文密码
                    user.getPassword(),
                    ByteSourceUtils.bytes(user.getSalt()),
                    this.getName());
            return simpleAuthenticationInfo;         //认证通过,返回SimpleAuthenticationInfo对象
        }
        return null; //返回null，认证失败
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获得身份信息
        /*
        这里只需查根据用户名查找到Set<String> roles角色集合；和根据用户名查找到Set<String> perms用户权限集合，可简化
        Set<String> pers = shiroUserService.getPersByUserId(shiroUser.getUserid());//根据uid获取权限
        Set<String> roles = shiroUserService.getRolesByUserId(shiroUser.getUserid());//根据uid获取角色
//        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        info.addRoles(roles);
//        info.addStringPermissions(pers);
         */
        String primaryPrincipal = (String) principalCollection.getPrimaryPrincipal();
        System.out.println("primaryPrincipal++++++++" + primaryPrincipal);
        UserDao userDao = (UserDao)ApplicationContextUtils.getBean("userDao");
        User user = userDao.findRolesByUserName(primaryPrincipal);  //得到用户的信息，且关联到对应的Role上
        //System.out.println("用户授权：" + user);
        //查询数据库中用户的角色表，得到结果集合，遍历集合给用户赋予角色信息
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
            List<Role> roles = user.getRoles();
            for (Role role : roles) {
                System.out.println("[1]:" + role);
                //给用户赋予角色
                simpleAuthorizationInfo.addRole(role.getName());

                //给用户赋值权限,
                List<Perm> perms = userDao.findPermsByRoleId(role.getId());
                //System.out.println("-----perms"+perms);
                if (!CollectionUtils.isEmpty(perms)) {
                    for (Perm perm : perms) {
                        System.out.println("[2]:" + perms);
//                        根据数据库中的存在提添加权限操作
                        simpleAuthorizationInfo.addStringPermission(perm.getName());
                    };
                }
            };

            return simpleAuthorizationInfo;
        }
        return null;
    }


}
