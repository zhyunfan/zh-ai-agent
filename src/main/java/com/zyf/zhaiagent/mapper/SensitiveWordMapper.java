package com.zyf.zhaiagent.mapper;

import com.zyf.zhaiagent.entity.SensitiveWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SensitiveWordMapper {

    /**
     * 查询所有启用的违禁词
     */
    @Select("SELECT word FROM sensitive_word WHERE status = 1")
    List<String> findAllEnabledWords();

    /**
     * 查询完整信息（用于扩展）
     */
    @Select("SELECT * FROM sensitive_word WHERE status = 1")
    List<SensitiveWord> findAllEnabled();
}