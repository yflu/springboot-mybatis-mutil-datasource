package com.eric.controller;

import com.alibaba.fastjson.JSON;
import com.eric.model.cluster.EngineCost;
import com.eric.service.EngineCostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by eric on 2017/9/21.
 */
@Controller
public class IndexController extends BaseController{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EngineCostService engineCostService;

    @RequestMapping("/")
    public String index() {
        logger.debug("This is a debug message");
        logger.info("This is an info message");
        logger.warn("This is a warn message");
        logger.error("This is an error message");
        return "index";
    }

    @RequestMapping("/list")
    public String list() {
        List<EngineCost> users = engineCostService.findAll();
        logger.info(JSON.toJSONString(users));
        return JSON.toJSONString(users);
    }

    @RequestMapping("/login")
    public String login() {
        return "index";
    }
}
