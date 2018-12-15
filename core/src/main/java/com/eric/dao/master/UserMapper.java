package com.eric.dao.master;

import com.eric.model.master.User;
import com.github.pagehelper.Page;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> findAll();

    List<User> findByPage();
}