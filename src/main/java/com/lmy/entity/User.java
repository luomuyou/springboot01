package com.lmy.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data   //所有变量的get和set方法
@NoArgsConstructor  //有参构造器
@AllArgsConstructor //无参构造器
@Accessors(chain = true)    //链式调用
@ToString   //toString方法
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String username;
    private String password;
    private String salt;
    //定义角色集合
    private List<Role> roles;
}
