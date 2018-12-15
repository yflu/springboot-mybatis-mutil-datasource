package com.eric.dao.cluster;

import com.eric.model.cluster.EngineCost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EngineCostMapper {
    int deleteByPrimaryKey(@Param("costName") String costName, @Param("engineName") String engineName, @Param("deviceType") Integer deviceType);

    int insert(EngineCost record);

    int insertSelective(EngineCost record);

    EngineCost selectByPrimaryKey(@Param("costName") String costName, @Param("engineName") String engineName, @Param("deviceType") Integer deviceType);

    int updateByPrimaryKeySelective(EngineCost record);

    int updateByPrimaryKey(EngineCost record);

    List<EngineCost> findAll();

    List<EngineCost> findByPage();
}