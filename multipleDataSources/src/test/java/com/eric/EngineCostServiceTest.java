package com.eric;

import com.alibaba.fastjson.JSON;
import com.eric.model.cluster.EngineCost;
import com.eric.service.EngineCostService;
import com.eric.util.PageInfo;
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
public class EngineCostServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EngineCostService engineCostService;

    @Test
    public void testFindAll() {
        List<EngineCost> users = engineCostService.findAll();
        logger.info(JSON.toJSONString(users));
    }

    @Test
    public void testFindByPage() {
        PageInfo<EngineCost> users = engineCostService.findByPage(2, 1);
        logger.info(JSON.toJSONString(users));
    }
}
