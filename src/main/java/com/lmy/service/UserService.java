package com.lmy.service;

import com.lmy.entity.Perm;
import com.lmy.entity.User;

import java.util.List;

public interface UserService {
    //注册用户
    void register(User user);
    //根据用户名查询
    User findByUserName(String userName);
    User findRolesByUserName(String username);
    //根据用户名查询角色
    List<Perm> findPermsByRoleId(String roleid);

    User login(User user);
}
