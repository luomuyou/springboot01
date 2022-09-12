package com.lmy.service;

import com.lmy.dao.UserDao;
import com.lmy.entity.Perm;
import com.lmy.entity.User;
import com.lmy.utils.SaltUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional  //处理事务回滚
public class UserServiceImpl implements UserService{
    @Autowired
    private UserDao userDao;
    @Override
    public void register(User user) {
        User users = userDao.findByUserName(user.getUsername());
        if(users != null){
            System.out.println("用户名已存在");
            throw new RuntimeException("用户名已存在");
        }

        //业务处理，转化密码名明文为密文 ，md5 + salt + hash 处理，一般将用户名作为salt
        //Md5Hash md5Hash = new Md5Hash(user.getPassword(),user.getUsername(),1024);
        String salt = SaltUtils.getSalt(8);     //获得得随机盐
        Md5Hash md5Hash = new Md5Hash(user.getPassword(), salt,1024);
        //生成的随机盐写入数据库用户表中
        user.setSalt(salt);
        user.setPassword(md5Hash.toHex());  //toHex()得到加密之后得密码
        System.out.println("intsert UserService:"+user);
        userDao.save(user);
    }

    @Override
    public User findByUserName(String userName) {
        return userDao.findByUserName(userName);
    }

    @Override
    public User findRolesByUserName(String username) {
        return null;
    }

    @Override
    public List<Perm> findPermsByRoleId(String roleId) {
        return userDao.findPermsByRoleId(roleId);
    }

    public User login(User user){
        System.out.println("UserServiceImpl111111  "+user);
        User userDB = userDao.login(user);
        System.out.println("UserServiceImpl2222222222  "+userDB);
        if(userDB != null){
            return userDB;
        }
        throw new RuntimeException("登录失败！");
    }
}
