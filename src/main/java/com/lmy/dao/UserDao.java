package com.lmy.dao;

import com.lmy.entity.Perm;
import com.lmy.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
    void save(User user);

    User findByUserName(String username);

    //根据用户名查询角色
    User findRolesByUserName(String username);
    //根据角色id查询权限
    List<Perm> findPermsByRoleId(String roleId);

    //JWT登录
    User login(User user);
}
