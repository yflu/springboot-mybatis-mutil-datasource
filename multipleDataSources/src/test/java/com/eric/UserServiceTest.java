package com.eric;

import com.alibaba.fastjson.JSON;
import com.eric.model.master.User;
import com.eric.service.UserService;
import com.eric.util.PageInfo;
import com.github.pagehelper.Page;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by eric on 2017/9/26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Test
    public void testInsert() {
        for (int i = 0; i < 10; i++) {
            User data = new User();
            data.setPassword("123456");
            data.setUsername("admin" + i);
            userService.insert(data);
            logger.debug(JSON.toJSONString(data));
        }
    }

    @Test
    public void testFindAll() {
        List<User> users = userService.findAll();
        logger.info(JSON.toJSONString(users));
    }

    @Test
    public void testFindByPage() {
        PageInfo<User> users = userService.findByPage(1, 5);
        logger.info(JSON.toJSONString(users));
    }
}
