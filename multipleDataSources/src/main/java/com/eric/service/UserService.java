package com.eric.service;


import com.eric.model.master.User;
import com.eric.util.PageInfo;

import java.util.List;

/**
 * Created by eric on 2017/9/26.
 */
public interface UserService {

    List<User> findAll();

    /**
     * 分页查询
     *
     * @param pageNo   页号
     * @param pageSize 每页显示记录数
     * @return
     */
    PageInfo<User> findByPage(Integer pageNo, Integer pageSize);

    void insert(User data);
}
