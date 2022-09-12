package com.lmy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data   //所有变量的get和set方法
@NoArgsConstructor  //有参构造器
@AllArgsConstructor //无参构造器
@ToString   //toString方法
public class Perm  implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String url;

}
