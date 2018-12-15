package com.eric.service.impl;

import com.eric.dao.cluster.EngineCostMapper;
import com.eric.model.cluster.EngineCost;
import com.eric.service.EngineCostService;
import com.eric.util.PageInfo;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by eric on 2017/9/26.
 */
@Service
public class EngineCostServiceImpl implements EngineCostService {

    @Autowired
    private EngineCostMapper engineCostMapper;


    public List<EngineCost> findAll() {
        return engineCostMapper.findAll();
    }

    public PageInfo<EngineCost> findByPage(Integer pageNo, Integer pageSize) {
        pageNo = pageNo == null ? 1 : pageNo;
        pageSize = pageSize == null ? 10 : pageSize;
        PageHelper.startPage(pageNo, pageSize);
        List<EngineCost> list = engineCostMapper.findByPage();
        return new PageInfo(list);
    }

    public void insert(EngineCost data) {

    }
}
