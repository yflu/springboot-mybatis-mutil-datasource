package com.eric.service.impl;

import com.eric.dao.master.UserMapper;
import com.eric.model.master.User;
import com.eric.service.UserService;
import com.eric.util.PageInfo;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by eric on 2017/9/26.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    public List<User> findAll() {
        return userMapper.findAll();
    }

    public PageInfo<User> findByPage(Integer pageNo, Integer pageSize) {
        pageNo = pageNo == null ? 1 : pageNo;
        pageSize = pageSize == null ? 10 : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        List<User> list = userMapper.findByPage();
        return new PageInfo(list);
    }

    public void insert(User data) {

    }
}
