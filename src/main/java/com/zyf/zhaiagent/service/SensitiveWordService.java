package com.zyf.zhaiagent.service;

import com.zyf.zhaiagent.mapper.mysql.SensitiveWordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensitiveWordService {
    
    private final SensitiveWordMapper mapper;
    
    /**
     * 从数据库加载违禁词（带缓存）
     */
    @Cacheable(value = "sensitiveWords", //缓存名（放到哪个缓存区）
            unless = "#result == null || #result.isEmpty()")//什么情况下不缓存
    public List<String> loadSensitiveWords() {
        log.info("从数据库加载违禁词列表...");
        List<String> words = mapper.findAllEnabledWords();
        log.info("加载完成，共 {} 个违禁词", words.size());
        return words;
    }
    
    /**
     * 刷新缓存（增删改后调用）
     */
    @CacheEvict(value = "sensitiveWords", allEntries = true)
    public void refreshCache() {
        log.info("违禁词缓存已刷新");
    }
    
    /**
     * 定时刷新缓存（每5分钟）
     */
    @Scheduled(fixedDelay = 300000)
    public void scheduledRefresh() {
        refreshCache();
    }
}